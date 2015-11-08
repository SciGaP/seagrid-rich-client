package org.seagrid.desktop.apis.airavata;

import org.apache.airavata.api.Airavata;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.error.AiravataErrorType;
import org.apache.airavata.model.experiment.ExperimentSearchFields;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.apache.airavata.model.security.AuthzToken;
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

    private AiravataManager() throws AiravataClientException {
        String host = "gw56.iu.xsede.org";
        int port = 10930;
        try {

            TTransport transport = new TSocket(host, port);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            this.airavataClient = new Airavata.Client(protocol);
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

    public List<ExperimentSummaryModel> getExperimentSummaries(Map<ExperimentSearchFields,String> filters, int limit, int offset){
        List<ExperimentSummaryModel> exp = new ArrayList<>();
        try{
            exp = getClient().searchExperiments(
                    getAuthzToken(), getGatewayId(), getUserName(), filters, limit, offset);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return exp;
    }

    public List<ExperimentSummaryModel> getExperimentSummariesInProject(String projectId){
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

    public List<ExperimentSummaryModel> getRecentExperimentSummaries(){
        List<ExperimentSummaryModel> exp = new ArrayList<>();
        try{
            Map<ExperimentSearchFields,String> filters = new HashMap<>();
            exp = getClient().searchExperiments(
                    getAuthzToken(), getGatewayId(), getUserName(), filters, 10, 0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return exp;
    }

    public List<Project> getProjects(){
        List<Project> projects = new ArrayList<>();
        try{
            projects = getClient().getUserProjects(
                    getAuthzToken(), getGatewayId(), getUserName(), -1, 0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return projects;
    }

    public Project createProject(String projectName, String projectDescription) {
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
}
