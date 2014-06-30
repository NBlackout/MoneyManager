package jobs.triggers;

import jobs.generators.RegularTransactionGenerator;
import jobs.reconciliers.RegularTransactionReconciler;
import jobs.synchronizers.AccountsSynchronizer;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import play.libs.F.Promise;

@On("0 0 * * * ?")
public class JobTrigger extends Job {

	@Override
	public void doJob() {
		Logger.info("BEGIN JobTrigger.doJob()");
		triggerJobs();
		Logger.info("  END JobTrigger.doJob()");
	}

	@Override
	public Promise<?> now() {
		Logger.info("BEGIN JobTrigger.now()");
		triggerJobs();
		Logger.info("  END JobTrigger.now()");

		return null;
	}

	private void triggerJobs() {
		Logger.info("BEGIN JobTrigger.triggerJobs()");
		new AccountsSynchronizer().now();
		new RegularTransactionGenerator().now();
		new RegularTransactionReconciler().now();
		Logger.info("  END JobTrigger.triggerJobs()");
	}
}
