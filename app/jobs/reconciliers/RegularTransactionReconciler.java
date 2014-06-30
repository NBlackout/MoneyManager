package jobs.reconciliers;

import models.transactions.regular.Configuration;
import play.jobs.Job;
import play.libs.F.Promise;

public class RegularTransactionReconciler extends Job {

	@Override
	public Promise<?> now() {
		return null;
	}

	private void reconcile(Configuration configuration) {
	}
}
