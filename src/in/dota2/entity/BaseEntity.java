package in.dota2.entity;

/**
 * Simple JavaBean domain object with an id property.
 * Used as a base class for objects needing this property.
 *
 */
public abstract class BaseEntity {

	Integer id;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public boolean isNew() {
		return (this.id == null);
	}

	public String getSerial(){
		return String.format("%06d", this.id);
	}
	
}
