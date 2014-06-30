package controllers;

import jobs.triggers.JobTrigger;

public class Application extends SuperController {

	public static void index() {
		render();
	}

	public static void hey() {
		new JobTrigger().now();

		index();
	}
}
