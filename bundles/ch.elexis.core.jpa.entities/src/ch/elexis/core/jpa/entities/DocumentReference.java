package ch.elexis.core.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ch.elexis.core.jpa.entities.converter.BooleanCharacterConverterSafe;
import ch.elexis.core.jpa.entities.id.ElexisIdGenerator;
import ch.elexis.core.jpa.entities.listener.EntityWithIdListener;


@Entity
@Table(name = "CH_ELEXIS_CORE_FINDINGS_DOCUMENTREFERENCE")
@EntityListeners(EntityWithIdListener.class)
@NamedQuery(name = "DocumentReference.patientid", query = "SELECT dr FROM DocumentReference dr WHERE dr.deleted = false AND dr.patientid = :patientid")
public class DocumentReference extends AbstractEntityWithId implements EntityWithId, EntityWithDeleted {

	// Transparently updated by the EntityListener
	protected Long lastupdate;
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@Column(unique = true, nullable = false, length = 25)
	private String id = ElexisIdGenerator.generateId();
	
	@Column
	@Convert(converter = BooleanCharacterConverterSafe.class)
	protected boolean deleted = false;
	
	@Override
	public boolean isDeleted(){
		return deleted;
	}
	
	@Override
	public void setDeleted(boolean deleted){
		this.deleted = deleted;
	}
	
	@Override
	public String getId(){
		return id;
	}
	
	@Override
	public void setId(String id){
		this.id = id;
	}
	
	@Override
	public Long getLastupdate(){
		return lastupdate;
	}
	
	@Override
	public void setLastupdate(Long lastupdate){
		this.lastupdate = lastupdate;
	}
	
	@Column(length = 80)
	private String patientid;

	@Column(length = 80)
	private String documentstoreid;

	@Column(length = 80)
	private String documentid;
	
	@Lob
	private String content;

	public String getPatientId() {
		return patientid;
	}

	public void setPatientId(String patientId) {
		this.patientid = patientId;
	}

	public String getDocumentStoreId(){
		return documentstoreid;
	}

	public void setDocumentStoreId(String documentstoreid){
		this.documentstoreid = documentstoreid;
	}

	public String getDocumentId(){
		return documentid;
	}

	public void setDocumentId(String documentid){
		this.documentid = documentid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}