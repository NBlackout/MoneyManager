package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import play.db.jpa.Model;
import play.i18n.Messages;

@Entity
@Table(name = "`Bank`")
public class Bank extends Model {

	private static final long serialVersionUID = -7683571188654748135L;

	public String number;

	public String label;

	@Enumerated
	public BankType type;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime lastSync;

	@OneToMany(mappedBy = "bank")
	public List<Customer> customers;

	public DateTime getLocalLastSync() {
		DateTime localLastSync = null;

		if (lastSync != null) {
			String id = Messages.get("dates.timezone.id");
			DateTimeZone local = DateTimeZone.forID(id);
			long millis = local.convertUTCToLocal(lastSync.getMillis());

			localLastSync = new DateTime(millis);
		}

		return localLastSync;
	}
}
