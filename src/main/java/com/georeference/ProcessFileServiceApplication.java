package com.georeference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProcessFileServiceApplication /*implements CommandLineRunner*/ {

	/*private final JobLauncher jobLauncher;
	private final ApplicationContext applicationContext;

	public ProcessFileServiceApplication(JobLauncher jobLauncher, ApplicationContext applicationContext) {
		this.jobLauncher = jobLauncher;
		this.applicationContext = applicationContext;
	}*/

	public static void main(String[] args) {
		SpringApplication.run(ProcessFileServiceApplication.class, args);
	}

	/*@Override
	public void run(String... args) throws Exception {

		Job job = (Job) applicationContext.getBean("insertIntoDbFromCsvJob");

		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("startAt", System.currentTimeMillis())
				.toJobParameters();

		var jobExecution = jobLauncher.run(job, jobParameters);

		var batchStatus = jobExecution.getStatus();
		while (batchStatus.isRunning()) {
			System.out.println("Still running...");
			Thread.sleep(5000L);
		}
	}*/

}
