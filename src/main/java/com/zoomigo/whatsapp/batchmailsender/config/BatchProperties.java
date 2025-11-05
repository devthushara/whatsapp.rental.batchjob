package com.zoomigo.whatsapp.batchmailsender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "batch")
public class BatchProperties {
    private List<JobConfig> jobs;

    public static class Schedule { public String cron; public Long fixedDelay; public Long fixedRate; public String getCron() {return cron;} public Long getFixedDelay() {return fixedDelay;} public Long getFixedRate() {return fixedRate;} public void setCron(String c){this.cron=c;} public void setFixedDelay(Long d){this.fixedDelay=d;} public void setFixedRate(Long r){this.fixedRate=r;} }
    public static class Pagination { public String mode = "LIMIT_OFFSET"; public String limitParam = "limit"; public String getMode() {return mode;} public String getLimitParam() {return limitParam;} }

    public static class TemplateRef { public String subject; public String body; public String getSubject(){return subject;} public String getBody(){return body;} public void setSubject(String s){this.subject=s;} public void setBody(String b){this.body=b;} }

    public static class JobConfig {
        private String name;
        private boolean enabled = true;
        private Schedule schedule = new Schedule();
        private String sql;
        private int batchSize = 100;
        private Pagination pagination = new Pagination();
        private int maxRetries = 3;
        private int concurrency = 1;
        private String markColumn = "batch_sent_at";
        private String attemptsColumn = "batch_attempts";
        private TemplateRef template = new TemplateRef();
        // Default to false so jobs run unless explicitly configured as dry-run
        private boolean dryRun = false;
        private Map<String,String> additional;

        public String getName(){return name;}
        public void setName(String n){this.name=n;}
        public boolean isEnabled(){return enabled;}
        public void setEnabled(boolean e){this.enabled=e;}
        public Schedule getSchedule(){return schedule;}
        public void setSchedule(Schedule s){this.schedule=s;}
        public String getSql(){return sql;}
        public void setSql(String sql){this.sql=sql;}
        public int getBatchSize(){return batchSize;}
        public void setBatchSize(int b){this.batchSize=b;}
        public Pagination getPagination(){return pagination;}
        public void setPagination(Pagination p){this.pagination=p;}
        public int getMaxRetries(){return maxRetries;}
        public void setMaxRetries(int r){this.maxRetries=r;}
        public int getConcurrency(){return concurrency;}
        public void setConcurrency(int c){this.concurrency=c;}
        public String getMarkColumn(){return markColumn;}
        public void setMarkColumn(String m){this.markColumn=m;}
        public String getAttemptsColumn(){return attemptsColumn;}
        public void setAttemptsColumn(String a){this.attemptsColumn=a;}
        public TemplateRef getTemplate(){return template;}
        public void setTemplate(TemplateRef t){this.template=t;}
        public boolean isDryRun(){return dryRun;}
        public void setDryRun(boolean d){this.dryRun=d;}
        public Map<String, String> getAdditional(){return additional;}
        public void setAdditional(Map<String, String> a){this.additional=a;}
    }

    public List<JobConfig> getJobs(){return jobs;}
    public void setJobs(List<JobConfig> jobs){this.jobs=jobs;}
}
