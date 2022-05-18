package org.seagrid.desktop.connectors.airavata;

import org.apache.airavata.api.Airavata;
import org.apache.airavata.model.appcatalog.appdeployment.ApplicationModule;
import org.apache.airavata.model.appcatalog.appinterface.ApplicationInterfaceDescription;
import org.apache.airavata.model.appcatalog.computeresource.ComputeResourceDescription;
import org.apache.airavata.model.appcatalog.userresourceprofile.UserComputeResourcePreference;
import org.apache.airavata.model.data.replica.DataProductModel;
import org.apache.airavata.model.data.replica.DataReplicaLocationModel;
import org.apache.airavata.model.error.*;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.experiment.ExperimentSearchFields;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.apache.airavata.model.security.AuthzToken;
import org.apache.airavata.model.status.JobStatus;
import org.apache.airavata.model.workspace.Notification;
import org.apache.airavata.model.workspace.Project;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class AiravataManager {

    private static AiravataManager instance;

    private Airavata.Client airavataClient;

    private AiravataCache<String, Object> airavataCache;

    private AiravataManager() throws AiravataClientException {
        try {
            this.airavataClient = createAiravataClient();
            this.airavataCache = new AiravataCache<>(200, 500, 50);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AiravataClientException(AiravataErrorType.UNKNOWN);
        }
    }

    public static AiravataManager getInstance() throws AiravataClientException {
        if (AiravataManager.instance == null) {
            AiravataManager.instance = new AiravataManager();
        }
        return AiravataManager.instance;
    }

    private Airavata.Client createAiravataClient() throws TTransportException {
        String host = SEAGridContext.getInstance().getAiravataHost();
        int port = SEAGridContext.getInstance().getAiravataPort();
        String applicationDataDir = System.getProperty("user.home") + File.separator + "SEAGrid" + File.separator;
        TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
        params.setTrustStore(applicationDataDir + "client-truststore.jks", "airavata");
        //System.setProperty("javax.net.ssl.trustStore","clientTrustStore.key");
        System.setProperty("javax.net.ssl.trustStore", applicationDataDir + "client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","airavata"); 
                TTransport transport = TSSLTransportFactory.getClientSocket(host, port, 10000, params);
                //TTransport transport = TSSLTransportFactory.getClientSocket(host, port, 10000);
        TProtocol protocol = new TBinaryProtocol(transport);
        //SharingRegistryService.Client sharingServiceClient = new SharingRegistryService.Client(protocol);
        return new Airavata.Client(protocol);
    }
//params.setKeyStore("/Library/Java/JavaVirtualMachines/jdk-16.0.1.jdk/Contents/Home/conf/security/java.security", "airavata");

    private Airavata.Client getClient() throws AiravataClientException, TTransportException {
        try{
            airavataClient.getAPIVersion();
        } catch (Exception e) {
            try{
                airavataClient = createAiravataClient();
                airavataClient.getAPIVersion();
                //airavataClient.getAPIVersion(getAuthzToken());
            } catch (Exception e1) {
                SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.LOGOUT, null));
                 AiravataClientException clientError = new AiravataClientException();
                 //clientError.setParameter("Unable to connect to the server at " + host + ":" + port);
                 clientError.setParameter("Unable to connect to the server ");
                 throw clientError;
                //SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.LOGOUT, null));
            }
        }
        return airavataClient;
    }

    private AuthzToken getAuthzToken() {
        AuthzToken authzToken = new AuthzToken(SEAGridContext.getInstance().getOAuthToken());
        authzToken.setClaimsMap(new HashMap<>());
        authzToken.getClaimsMap().put("gatewayID", SEAGridContext.getInstance().getAiravataGatewayId());
        authzToken.getClaimsMap().put("userName", SEAGridContext.getInstance().getUserName());
        return authzToken;
    }

    private String getGatewayId() {
        return SEAGridContext.getInstance().getAiravataGatewayId();
    }

    private String getUserName() {
        return SEAGridContext.getInstance().getUserName();
    }

    public synchronized List<ExperimentSummaryModel> getExperimentSummaries(Map<ExperimentSearchFields, String> filters,
                                                                            int limit, int offset) throws TException {
        List<ExperimentSummaryModel> exp = getClient().searchExperiments(
                getAuthzToken(), getGatewayId(), getUserName(), filters, limit, offset);
        return exp;
    }

    public synchronized List<ExperimentSummaryModel> getExperimentSummariesInProject(String projectId) throws TException {
        List<ExperimentSummaryModel> exp;
        Map<ExperimentSearchFields, String> filters = new HashMap<>();
        filters.put(ExperimentSearchFields.PROJECT_ID, projectId);
        exp = getClient().searchExperiments(
                getAuthzToken(), getGatewayId(), getUserName(), filters, -1, 0);
        return exp;
    }

    public synchronized List<ExperimentSummaryModel> getRecentExperimentSummaries(int limit) throws TException {
        List<ExperimentSummaryModel> exp;

        Map<ExperimentSearchFields, String> filters = new HashMap<>();
        exp = getClient().searchExperiments(
                getAuthzToken(), getGatewayId(), getUserName(), filters, limit, 0);
        return exp;
    }

    public synchronized List<Project> getProjects() throws TException {
        List<Project> projects;
        try{
            projects = getClient().getUserProjects(
                    getAuthzToken(), getGatewayId(), getUserName(), -1, 0);
        }catch (Exception ex){
            //FIXME If the user is new getProjects will fail
            getClient().createProject(getAuthzToken(),getGatewayId(),new Project("", getUserName(),
                    SEAGridContext.getInstance().getAiravataGatewayId(), "Default Project"));
            projects = getClient().getUserProjects(
                    getAuthzToken(), getGatewayId(), getUserName(), -1, 0);
        }
        return projects;
    }

    public synchronized Project createProject(String projectName, String projectDescription) throws TException {
        Project project;
        project = new Project("no-id", getUserName(), SEAGridContext.getInstance().getAiravataGatewayId(), projectName);
        if (projectDescription != null)
            project.setDescription(projectDescription);
        String projectId = getClient().createProject(
                getAuthzToken(), getGatewayId(), project);
        project.setProjectID(projectId);

        return project;
    }

    public synchronized ExperimentModel getExperiment(String experimentId) throws TException {
        return getClient().getExperiment(getAuthzToken(), experimentId);
    }

    public synchronized ComputeResourceDescription getComputeResource(String resourceId) throws TException {
        ComputeResourceDescription computeResourceDescription;

        if (airavataCache.get(resourceId) != null) {
            computeResourceDescription = (ComputeResourceDescription) airavataCache.get(resourceId);
        } else {
            computeResourceDescription = getClient().getComputeResource(getAuthzToken(), resourceId);
            airavataCache.put(resourceId, computeResourceDescription);
        }
        return computeResourceDescription;
    }

    public synchronized ApplicationInterfaceDescription getApplicationInterface(String interfaceId) throws TException {
        ApplicationInterfaceDescription applicationInterfaceDescription = null;

        if (airavataCache.get(interfaceId) != null) {
            applicationInterfaceDescription = (ApplicationInterfaceDescription) airavataCache.get(interfaceId);
        } else {
            applicationInterfaceDescription = getClient().getApplicationInterface(getAuthzToken(), interfaceId);
            airavataCache.put(interfaceId, applicationInterfaceDescription);
        }

        return applicationInterfaceDescription;
    }

    public synchronized Project getProject(String projectId) throws TException {
        Project project;
        if (airavataCache.get(projectId) != null) {
            project = (Project) airavataCache.get(projectId);
        } else {
            project = getClient().getProject(getAuthzToken(), projectId);
            airavataCache.put(projectId, project);
        }
        return project;
    }

    public synchronized Map<String, JobStatus> getJobStatuses(String expId) throws TException {
        Map<String, JobStatus> jobStatuses;
        jobStatuses = getClient().getJobStatuses(getAuthzToken(), expId);
        return jobStatuses;
    }

    public synchronized List<ApplicationInterfaceDescription> getAllApplicationInterfaces() throws TException {
        List<ApplicationInterfaceDescription> allApplicationInterfaces;
        allApplicationInterfaces = getClient().getAllApplicationInterfaces(getAuthzToken(), getGatewayId());
        Collections.sort(allApplicationInterfaces, (o1, o2) -> o1.getApplicationName().compareTo(o2.getApplicationName()));
        return allApplicationInterfaces;
    }

    public synchronized List<ComputeResourceDescription> getAvailableComputeResourcesForApp(String applicationInterfaceId)
            throws TException {
        List<ComputeResourceDescription> availableComputeResources;
        Map<String, String> temp = getClient().getAvailableAppInterfaceComputeResources(getAuthzToken(), applicationInterfaceId);
        availableComputeResources = new ArrayList<>();
        for (String resourceId : temp.keySet()) {
            availableComputeResources.add(getComputeResource(resourceId));
        }
        return availableComputeResources;
    }

    public synchronized String createExperiment(ExperimentModel experimentModel) throws TException {
        return getClient().createExperiment(getAuthzToken(), getGatewayId(), experimentModel);
    }

    public synchronized void updateExperiment(ExperimentModel experimentModel) throws TException {
        getClient().updateExperiment(getAuthzToken(), experimentModel.getExperimentId(), experimentModel);
    }

    public synchronized void launchExperiment(String experimentId) throws TException {
        getClient().launchExperiment(getAuthzToken(), experimentId, getGatewayId());
    }

    public synchronized void deleteExperiment(String experimentId) throws TException {
        getClient().deleteExperiment(getAuthzToken(), experimentId);
    }

    public synchronized void cancelExperiment(String experimentId) throws TException {
        getClient().terminateExperiment(getAuthzToken(),experimentId, getGatewayId());
    }

    public synchronized String cloneExperiment(String experimentId, String newExpName, String projectId) throws TException {
        return getClient().cloneExperiment(getAuthzToken(), experimentId, newExpName, projectId);
    }

    public String registerDataProduct(DataProductModel dpModel) throws TException {
        return getClient().registerDataProduct(getAuthzToken(), dpModel);
    }

    public List<DataReplicaLocationModel> getDataReplicas(String uri) throws TException {
        return getClient().getDataProduct(getAuthzToken(), uri).getReplicaLocations();
    }

    public List<Notification> getNotifications() throws TException {
        //We don't need security token for calling getNotifications API method
        java.util.List<Notification> temp = (createAiravataClient()).getAllNotifications(new AuthzToken("dummy-token"),
                SEAGridContext.getInstance().getAiravataGatewayId());
        java.util.List<Notification> messages = new ArrayList<>();
        if(temp != null){
            messages.addAll(temp.stream().filter(m -> m.getExpirationTime() > System.currentTimeMillis()
                    || m.getExpirationTime() == 0).collect(Collectors.toList()));
        }
        return messages;
    }

    public UserComputeResourcePreference getUserComputeResourcePrefs(String computeResourceId) throws TException {
        UserComputeResourcePreference userComputeResourcePreference = null;
        try{
            userComputeResourcePreference = getClient().getUserComputeResourcePreference(getAuthzToken(),
                    SEAGridContext.getInstance().getUserName(), SEAGridContext.getInstance().getAiravataGatewayId(),
                    computeResourceId);
            return userComputeResourcePreference;
        }catch (Exception ex) {
            //User compute resource pref does not exists
            return null;
        }
    }

    public List<ApplicationModule> getAccessibleAppModules() throws TException {
        return getClient().getAccessibleAppModules(getAuthzToken(), getGatewayId());
    }
}
