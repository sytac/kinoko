package com.sytac.caseapocalypse.service;

import ch.qos.logback.core.CoreConstants;
import com.sytac.caseapocalypse.model.db.GitHubMember;
import com.sytac.caseapocalypse.model.Team;
import com.sytac.caseapocalypse.service.GithubService;
import com.sytac.caseapocalypse.model.Permissions;
import com.sytac.caseapocalypse.service.exception.GitHubServiceException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Component
public class GithubService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GithubService.class);

    private RestTemplate restTemplate;

    /**
     * Username to use the GitHub API
     */
    @Value("${github.credentials.username}")
    private String USERNAME;

    /**
     * Password to use the GitHub API
     */
    @Value("${github.credentials.password}")
    private String PASSWORD;

    /**
     * Connection timeout of the http client for calling the GitHub API
     */
    @Value("${github.httpclient.timeout.connection}")
    private int CONNECTION_TIMEOUT;

    /**
     * Read timeout of the http client for calling the GitHub API
     */
    @Value("${github.httpclient.timeout.reader}")
    private int READ_TIMEOUT;

    /**
     * The url of the GitHub API
     */
    @Value("${github.url.api}")
    private String GITHUB_API_URL;

    /**
     * Inizialize the HTTP client with Basic Authentication
     */
    @PostConstruct
    private void builHTTPClient() {
        restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor(USERNAME, PASSWORD));

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        restTemplate.setRequestFactory(requestFactory);
    }

    /**
     * Check if the Repository exists in GitHub for a given Owner
     *
     * @param ownerName      the name of the owner
     * @param repositoryName the name of the repository
     * @return true if the repo exists, false others.
     * @throws GitHubServiceException
     */
    public boolean repoExists(String ownerName, String repositoryName) throws GitHubServiceException {

        try {
            // set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(null, headers);

            // send request and parse result
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    GITHUB_API_URL + "/repos/" + ownerName + "/" + repositoryName,
                    HttpMethod.GET,
                    entity,
                    String.class);

            LOGGER.debug("GitHub call to check if the Repository exists. Http response: " + responseEntity.getStatusCode() + " , Body: " + responseEntity.getBody());

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return true;
            }
        } catch (HttpClientErrorException e) {
            HttpStatus statusCode = e.getStatusCode();
            if (statusCode.is4xxClientError()) {
                return false;
            } else {
                LOGGER.error("Error calling GitHub API, to check if the Repository exists " + e);
                throw new GitHubServiceException("Error calling GitHub API, to check if the Repository exists", e);
            }
        }
        return false;
    }

    /**
     * Check is the main folder of a repository is empty or not
     *
     * @param ownerName
     * @param repositoryName
     * @return
     * @throws GitHubServiceException
     */
    public boolean isRepoEmpty(String ownerName, String repositoryName) throws GitHubServiceException {

        try {
            // set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(null, headers);

            // send request and parse result
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    GITHUB_API_URL + "/repos/" + ownerName + "/" + repositoryName + "/contents/",
                    HttpMethod.GET,
                    entity,
                    String.class);

            LOGGER.debug("GitHub call to retrieve the contents of a Repository. Http response: " + responseEntity.getStatusCode() + " , Body: " + responseEntity.getBody());

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                //If the repo is empty then the http response is 404:
                // {
                //  "message": "Not Found",
                //   "documentation_url": "https://developer.github.com/v3"
                // }

                //If the repo in not empty the http response is 200 with a Json Array
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(responseEntity.getBody());
                JSONArray contentsJSON = (JSONArray) obj;
                if (contentsJSON.size() > 0) {
                    //files and folders are present in the repository
                    return false;
                }
            }
        } catch (ParseException e) {
            LOGGER.error("Error parsing GitHub API response, to retrieve the contents of a Repository " + e);
            throw new GitHubServiceException("Error parsing GitHub API response, to retrieve the contents of a Repository ", e);
        } catch (HttpClientErrorException e) {
            HttpStatus statusCode = e.getStatusCode();
            if (statusCode.is4xxClientError()) {
                return true;
            } else {
                LOGGER.error("Error calling GitHub API, to retrieve the contents of a Repository " + e);
                throw new GitHubServiceException("Error calling GitHub API, to retrieve the contents of a Repository", e);
            }
        }
        return true;
    }

    /**
     * Calls the GitHub API to retrieve all the Teams belonging to an
     * Organization
     *
     * @param organizationName the name of the Organization
     * @return the teams of an Organization
     * @throws GitHubServiceException
     */
    public List<Team> getTeams(String organizationName) throws GitHubServiceException {

        List<Team> teams = new ArrayList<>();

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "vnd.github.hellcat-preview+json"));
        headers.setAccept(Arrays.asList(new MediaType("application", "vnd.github.hellcat-preview+json")));
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        try {
            // send request and parse result
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    GITHUB_API_URL + "/orgs/" + organizationName + "/teams",
                    HttpMethod.GET,
                    entity,
                    String.class);

            LOGGER.debug("GitHub call to retrieve Team. Http response: " + responseEntity.getStatusCode() + " , Body: " + responseEntity.getBody());

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(responseEntity.getBody());

            JSONArray teamsJSON = (JSONArray) obj;
            Iterator<JSONObject> iterator = teamsJSON.iterator();
            while (iterator.hasNext()) {
                JSONObject teamJSON = iterator.next();
                Team team = new Team();
                team.setId(teamJSON.get("id").toString());
                team.setName(teamJSON.get("name").toString());
                teams.add(team);
            }
        } catch (ParseException e) {
            LOGGER.error("Error parsing GitHub API response, to retrieve Team " + e);
            throw new GitHubServiceException("Error parsing GitHub API response, to retrieve Team", e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error calling GitHub API, to retrieve Team " + e);
            throw new GitHubServiceException("Error calling GitHub API, to retrieve Team ", e);
        }
        return teams;
    }

    /**
     * Retrieve the Team's id by the Team's name
     *
     * @param teams    the List of Team where to search
     * @param teamName the name of the desired Team
     * @return the Team's id
     */
    public String getTeamId(List<Team> teams, String teamName) {

        for (Team team : teams) {
            if (teamName.equals(team.getName())) {
                return team.getId();
            }
        }
        return null;
    }

    /**
     * Call the GitHub API to retrieve all the memebers of a specific Team
     * Return the GitHub username of the Team's members.
     *
     * @param teamId the id of the Team
     * @return all the members of the Team
     * @throws GitHubServiceException
     */
    public List<GitHubMember> getTeamMembers(String teamId) throws GitHubServiceException {
        if (teamId == null) {
            return new ArrayList<GitHubMember>();
        }

        List<GitHubMember> members = new ArrayList<>();

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "vnd.github.hellcat-preview+json"));
        headers.setAccept(Arrays.asList(new MediaType("application", "vnd.github.hellcat-preview+json")));
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        // send request and parse result
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                GITHUB_API_URL + "/teams/" + teamId + "/members",
                HttpMethod.GET,
                entity,
                String.class);

        LOGGER.debug("GitHub call to retrieve Team's members. Http response: " + responseEntity.getStatusCode() + " , Body: " + responseEntity.getBody());

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(responseEntity.getBody());
            JSONArray membersJSON = (JSONArray) obj;
            Iterator<JSONObject> iterator = membersJSON.iterator();
            while (iterator.hasNext()) {
                JSONObject memberJSON = iterator.next();
                GitHubMember member = new GitHubMember();
                member.setUserName(memberJSON.get("login").toString());
                members.add(member);
            }

        } catch (ParseException e) {
            LOGGER.error("Error parsing GitHub API respons, to retrieve Team's members  " + e);
            throw new GitHubServiceException("Error parsing GitHub API response, to retrieve Team's members", e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error calling GitHub API, to retrieve Team's members " + e);
            throw new GitHubServiceException("Error calling GitHub API, to retrieve Team's members ", e);
        }
        return members;
    }

    /**
     * Add a collaborator to a Repository.
     * It's possibile to give differents role to the collaborator
     *
     * @param username    the GitHub username of the collaborator
     * @param owner       the GitHub Company of the collaborator
     * @param repository  the name of the Repository
     * @param permissions the permission for the collaborator
     * @throws GitHubServiceException
     */
    public void addCollaborator(String username, String owner, String repository, Permissions permissions) throws GitHubServiceException {

        try {
            // set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(null, headers);

            // send request and parse result
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    GITHUB_API_URL + "/repos/" + owner + "/" + repository + "/collaborators/" + username + "?permission=" + permissions.get(),
                    HttpMethod.PUT,
                    entity,
                    String.class);

            if(responseEntity.getStatusCode() != HttpStatus.CREATED) {
                String errorMessage = "Failed adding a new Collaborator in a Team. Probably the quota on GitHub was exceeded.";
                LOGGER.error(errorMessage);
                throw new GitHubServiceException(errorMessage);
            }

            LOGGER.debug("GitHub call to add a Collaborator in a Team. Http response: " + responseEntity.getStatusCode());
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error calling GitHub API, to add a Collaborator in a Team " + e);
            throw new GitHubServiceException("Error calling GitHub API, to add a Collaborator in a Team ", e);
        }
    }


    /**
     * Remove a collaborator from a Repository.
     *
     * @param username   the GitHub username of the collaborator
     * @param owner      the GitHub Company of the collaborator
     * @param repository the name of the Repository
     * @throws GitHubServiceException
     */
    public void removeCollaborator(String username, String owner, String repository) throws GitHubServiceException {

        try {
            // set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(null, headers);

            // send request and parse result
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    GITHUB_API_URL + "/repos/" + owner + "/" + repository + "/collaborators/" + username,
                    HttpMethod.DELETE,
                    entity,
                    String.class);

            LOGGER.debug("GitHub call to remove a Collaborator in a Repository. Http response: " + responseEntity.getStatusCode());
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error calling GitHub API, to remove a Collaborator in a Repository " + e);
            throw new GitHubServiceException("Error calling GitHub API, to remove a Collaborator in a Repository ", e);
        }
    }

    /**
     * Check if user is a collaborator in a Repository.
     *
     * @param username   the GitHub username of the collaborator
     * @param owner      the GitHub Company of the collaborator
     * @param repository the name of the Repository
     * @throws GitHubServiceException
     */
    public boolean isCollaborator(String username, String owner, String repository) throws GitHubServiceException {

        try {
            // set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(null, headers);

            // send request and parse result
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    GITHUB_API_URL + "/repos/" + owner + "/" + repository + "/collaborators/" + username,
                    HttpMethod.GET,
                    entity,
                    String.class);

            LOGGER.debug("GitHub call to check if a user is a Collaborator in a Repository. Http response: " + responseEntity.getStatusCode());
            if (HttpStatus.NO_CONTENT.equals(responseEntity.getStatusCode())) {
                LOGGER.debug("User " + username + " is a collaborator for the Repository " + repository);
                return true;
            } else {
                LOGGER.debug("User " + username + " is NOT a collaborator for the Repository " + repository);
                return false;
            }
        } catch (HttpClientErrorException e) {
            HttpStatus statusCode = e.getStatusCode();
            if (statusCode.is4xxClientError()) {
                return false;
            } else {
                LOGGER.error("Error calling GitHub API, to check if a user is a Collaborator in a Repository " + e);
                throw new GitHubServiceException("Error calling GitHub API, to check if a user is a Collaborator in a Repository ", e);
            }
        }
    }


    /**
     * Create a private Repository on the GitHub profile of an Organization
     *
     * @param organizationName the name of the Organization
     * @param repository       the name of the repository
     */
    public void createRepo(String organizationName, String repository) throws GitHubServiceException {

        try {
            // create request body
            String requestJson = "{"
                    + "\"name\":\"" + repository + "\","
                    + "\"private\":true"
                    + "}";

            // set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);

            // send request and parse result
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    GITHUB_API_URL + "/orgs/" + organizationName + "/repos",
                    HttpMethod.POST,
                    entity,
                    String.class);

            LOGGER.debug("GitHub call to create a Repository. Http response: " + responseEntity.getStatusCode());
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error calling GitHub API, to create a Repository " + e);
            throw new GitHubServiceException("Error calling GitHub API, to create a Repository ", e);
        }
    }

    /**
     * Clone the repository of the DevCase (frontend or backend) and push on
     * candidtae's repository the code STEPS: create folder for the user git
     * clone --bare
     * https://USERNAME:PASSWORD@github.com/Sytac-DevCase/sytac-{backend/frontend}-assignment.git
     * git push --mirror
     * https://USERNAME:PASSWORD@github.com/Sytac-DevCase/sytac-{backend/frontend}-assessment-{candidateGithubUsername}.git
     * delete the user's folder
     *
     * @param owner              the Organization's name
     * @param repositoryToClone  the Repositopry's name
     * @param repositoryToCreate the candiodate GitHub name
     */
    public void cloneAndPushMirror(String owner, String repositoryToClone, String repositoryToCreate) throws
            GitHubServiceException {

        LOGGER.debug("Executing bash script for repository " + repositoryToCreate);

        String[] cmd = {
                "/bin/sh",
                "-c",
                generateScript(owner, repositoryToClone, repositoryToCreate, USERNAME, PASSWORD)
        };
        System.out.println(cmd[2]);
        execute(cmd);
    }

    private String generateScript(String owner, String repositoryToClone, String repositoryToCreate, String username, String password) {
        System.out.println("Creating the repo for____: owner: " + owner + " repository: " + repositoryToCreate + " username:" + username + " password:" + password);
        return new StringBuilder()
                .append("echo cript_started")
                .append(" ; ")
//                .append("ls")
//                .append(" ; ")
                .append("mkdir ").append(repositoryToCreate)
                .append(" ; ")
                .append("echo dir_created")
                .append(" ; ")
//                .append("ls")
//                .append(" ; ")
                .append("cd ").append(repositoryToCreate)
                .append(" ; ")
//                .append("ls")
//                .append(" ; ")
                .append("echo inside_dir")
                .append(" ; ")
                .append("git clone --bare https://").append(username).append(":").append(password).append("@github.com/").append(owner).append("/").append(repositoryToClone).append(".git")
                .append(" ; ")
//                .append("ls")
//                .append(" ; ")
                .append("echo repo_cloned")
                .append(" ; ")
                .append("cd ").append(repositoryToClone).append(".git")
                .append(" ; ")
//                .append("ls")
//                .append(" ; ")
                .append("echo inside_repo_dir")
                .append(" ; ")
                .append("git push --mirror https://").append(username).append(":").append(password).append("@github.com/").append(owner).append("/").append(repositoryToCreate).append(".git")
                .append(" ; ")
//                .append("ls")
//                .append(" ; ")
                .append("echo repo_pushed")
                .append(" ; ")
                .append("rm -rf ../../").append(repositoryToCreate)
                .append(" ; ")
//                .append("ls")
//                .append(" ; ")
                .append("echo dir_deleted")
                .append(" ; ")
                .append("echo script_finished")
                .toString();
    }

    /**
     * execute a Shell/Bash script by JAVA
     *
     * @param command
     */
    private void execute(String[] command) throws GitHubServiceException {
        try {
            final Process p = Runtime.getRuntime().exec(command);

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;

            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            p.waitFor();
            LOGGER.debug("script finished");

        } catch (IOException | InterruptedException e) {
            LOGGER.error("executing script sh " + e);
            throw new GitHubServiceException("Error executing script sh ", e);
        }
    }

}
