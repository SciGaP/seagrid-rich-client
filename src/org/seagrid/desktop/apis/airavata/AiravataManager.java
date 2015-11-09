package org.seagrid.desktop.apis.airavata;

import javafx.print.PrinterJob;
import org.apache.airavata.api.Airavata;
import org.apache.airavata.model.appcatalog.appinterface.ApplicationInterfaceDescription;
import org.apache.airavata.model.appcatalog.computeresource.ComputeResourceDescription;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.error.AiravataErrorType;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.experiment.ExperimentSearchFields;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.apache.airavata.model.security.AuthzToken;
import org.apache.airavata.model.status.JobStatus;
import org.apache.airavata.model.workspace.Project;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiravataManager {

    private static AiravataManager instance;

    private Airavata.Client airavataClient;

    private AiravataCache<String, Object> airavataCache;

    private AiravataManager() throws AiravataClientException {
        String host = "gw56.iu.xsede.org";
        int port = 10930;
        try {

            TTransport transport = new TSocket(host, port);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            this.airavataClient = new Airavata.Client(protocol);

            this.airavataCache = new AiravataCache<>(200,500,6);
        } catch (TTransportException e) {
            throw new AiravataClientException(AiravataErrorType.UNKNOWN);
        }
    }

    public static AiravataManager getInstance() throws AiravataClientException {
        if(AiravataManager.instance == null){
            AiravataManager.instance = new AiravataManager();
        }
        return AiravataManager.instance;
    }

    private Airavata.Client getClient() throws AiravataClientException {
        return airavataClient;
    }

    private AuthzToken getAuthzToken() {
        return new AuthzToken("");
    }

    private String getGatewayId(){
        return "default";
    }

    private String getUserName(){
        return "master";
    }

    public synchronized List<ExperimentSummaryModel> getExperimentSummaries(Map<ExperimentSearchFields,String> filters, int limit, int offset){
        List<ExperimentSummaryModel> exp = new ArrayList<>();
        try{
            exp = getClient().searchExperiments(
                    getAuthzToken(), getGatewayId(), getUserName(), filters, limit, offset);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return exp;
    }

    public synchronized List<ExperimentSummaryModel> getExperimentSummariesInProject(String projectId){
        List<ExperimentSummaryModel> exp = new ArrayList<>();
        try{
            Map<ExperimentSearchFields,String> filters = new HashMap<>();
            filters.put(ExperimentSearchFields.PROJECT_ID, projectId);
            exp = getClient().searchExperiments(
                    getAuthzToken(), getGatewayId(), getUserName(), filters, -1, 0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return exp;
    }

    public synchronized List<ExperimentSummaryModel> getRecentExperimentSummaries(){
        List<ExperimentSummaryModel> exp = new ArrayList<>();
        try{
            Map<ExperimentSearchFields,String> filters = new HashMap<>();
            exp = getClient().searchExperiments(
                    getAuthzToken(), getGatewayId(), getUserName(), filters, 20, 0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return exp;
    }

    public synchronized List<Project> getProjects(){
        List<Project> projects = new ArrayList<>();
        try{
            projects = getClient().getUserProjects(
                    getAuthzToken(), getGatewayId(), getUserName(), -1, 0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return projects;
    }

    public synchronized Project createProject(String projectName, String projectDescription) {
        Project project = null;
        try{
            project = new Project("no-id",getUserName(),projectName);
            if(projectDescription !=null)
                project.setDescription(projectDescription);
            String projectId = getClient().createProject(
                    getAuthzToken(), getGatewayId(), project);
            project.setProjectID(projectId);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return project;
    }

    public synchronized ExperimentModel getExperiment(String experimentId) {
        ExperimentModel experiment = null;
        try{
            experiment = getClient().getExperiment(getAuthzToken(),experimentId);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return experiment;
    }

    public synchronized ComputeResourceDescription getComputeResource(String resourceId){
        ComputeResourceDescription computeResourceDescription = null;
        try{
            if(airavataCache.get(resourceId) != null) {
                computeResourceDescription = (ComputeResourceDescription)airavataCache.get(resourceId);
            }else{
                computeResourceDescription = getClient().getComputeResource(getAuthzToken(),resourceId);
                airavataCache.put(resourceId,computeResourceDescription);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return computeResourceDescription;
    }

    public synchronized ApplicationInterfaceDescription getApplicationInterface(String interfaceId){
        ApplicationInterfaceDescription applicationInterfaceDescription = null;
        try{
            if(airavataCache.get(interfaceId) != null) {
                applicationInterfaceDescription = (ApplicationInterfaceDescription)airavataCache.get(interfaceId);
            }else{
                applicationInterfaceDescription = getClient().getApplicationInterface(getAuthzToken(), interfaceId);
                airavataCache.put(interfaceId,applicationInterfaceDescription);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return applicationInterfaceDescription;
    }

    public synchronized Project getProject(String projectId){
        Project project = null;
        try{
            if(airavataCache.get(projectId) != null) {
                project = (Project)airavataCache.get(projectId);
            }else{
                project = getClient().getProject(getAuthzToken(), projectId);
                airavataCache.put(projectId,project);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return project;
    }

    public synchronized Map<String,JobStatus> getJobStatuses(String expId){
        Map<String,JobStatus> jobStatuses = null;
        try{
            jobStatuses = getClient().getJobStatuses(getAuthzToken(),expId);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return jobStatuses;
    }
}
