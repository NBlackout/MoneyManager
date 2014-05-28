package jobs.triggers;

import jobs.generators.RegularTransactionGenerator;
import jobs.synchronizers.AccountsSynchronizer;
import jobs.synchronizers.TransactionsSynchronizer;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

//@On("0 0 0 * * ?")
@Every("5mn")
public class JobTrigger extends Job {

	@Override
	public void doJob() {
		Logger.info("BEGIN JobTrigger.doJob()");
		new AccountsSynchronizer().now();
		new TransactionsSynchronizer().now();
		new RegularTransactionGenerator().now();
		Logger.info("  END JobTrigger.doJob()");
	}
}
