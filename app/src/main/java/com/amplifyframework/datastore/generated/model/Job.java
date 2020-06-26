package com.amplifyframework.datastore.generated.model;


import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Job type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Jobs")
public final class Job implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField TITLE = field("title");
  public static final QueryField OWNER = field("owner");
  public static final QueryField PHONE_NUMBER = field("phoneNumber");
  public static final QueryField ADDRESS = field("address");
  public static final QueryField STATUS = field("status");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String title;
  private final @ModelField(targetType="String", isRequired = true) String owner;
  private final @ModelField(targetType="String", isRequired = true) String phoneNumber;
  private final @ModelField(targetType="String", isRequired = true) String address;
  private final @ModelField(targetType="JobStatus", isRequired = true) JobStatus status;
  public String getId() {
      return id;
  }
  
  public String getTitle() {
      return title;
  }
  
  public String getOwner() {
      return owner;
  }
  
  public String getPhoneNumber() {
      return phoneNumber;
  }
  
  public String getAddress() {
      return address;
  }
  
  public JobStatus getStatus() {
      return status;
  }
  
  private Job(String id, String title, String owner, String phoneNumber, String address, JobStatus status) {
    this.id = id;
    this.title = title;
    this.owner = owner;
    this.phoneNumber = phoneNumber;
    this.address = address;
    this.status = status;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Job job = (Job) obj;
      return ObjectsCompat.equals(getId(), job.getId()) &&
              ObjectsCompat.equals(getTitle(), job.getTitle()) &&
              ObjectsCompat.equals(getOwner(), job.getOwner()) &&
              ObjectsCompat.equals(getPhoneNumber(), job.getPhoneNumber()) &&
              ObjectsCompat.equals(getAddress(), job.getAddress()) &&
              ObjectsCompat.equals(getStatus(), job.getStatus());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getTitle())
      .append(getOwner())
      .append(getPhoneNumber())
      .append(getAddress())
      .append(getStatus())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Job {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("title=" + String.valueOf(getTitle()) + ", ")
      .append("owner=" + String.valueOf(getOwner()) + ", ")
      .append("phoneNumber=" + String.valueOf(getPhoneNumber()) + ", ")
      .append("address=" + String.valueOf(getAddress()) + ", ")
      .append("status=" + String.valueOf(getStatus()))
      .append("}")
      .toString();
  }
  
  public static TitleStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static Job justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Job(
      id,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      title,
      owner,
      phoneNumber,
      address,
      status);
  }
  public interface TitleStep {
    OwnerStep title(String title);
  }
  

  public interface OwnerStep {
    PhoneNumberStep owner(String owner);
  }
  

  public interface PhoneNumberStep {
    AddressStep phoneNumber(String phoneNumber);
  }
  

  public interface AddressStep {
    StatusStep address(String address);
  }
  

  public interface StatusStep {
    BuildStep status(JobStatus status);
  }
  

  public interface BuildStep {
    Job build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements TitleStep, OwnerStep, PhoneNumberStep, AddressStep, StatusStep, BuildStep {
    private String id;
    private String title;
    private String owner;
    private String phoneNumber;
    private String address;
    private JobStatus status;
    @Override
     public Job build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Job(
          id,
          title,
          owner,
          phoneNumber,
          address,
          status);
    }
    
    @Override
     public OwnerStep title(String title) {
        Objects.requireNonNull(title);
        this.title = title;
        return this;
    }
    
    @Override
     public PhoneNumberStep owner(String owner) {
        Objects.requireNonNull(owner);
        this.owner = owner;
        return this;
    }
    
    @Override
     public AddressStep phoneNumber(String phoneNumber) {
        Objects.requireNonNull(phoneNumber);
        this.phoneNumber = phoneNumber;
        return this;
    }
    
    @Override
     public StatusStep address(String address) {
        Objects.requireNonNull(address);
        this.address = address;
        return this;
    }
    
    @Override
     public BuildStep status(JobStatus status) {
        Objects.requireNonNull(status);
        this.status = status;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String title, String owner, String phoneNumber, String address, JobStatus status) {
      super.id(id);
      super.title(title)
        .owner(owner)
        .phoneNumber(phoneNumber)
        .address(address)
        .status(status);
    }
    
    @Override
     public CopyOfBuilder title(String title) {
      return (CopyOfBuilder) super.title(title);
    }
    
    @Override
     public CopyOfBuilder owner(String owner) {
      return (CopyOfBuilder) super.owner(owner);
    }
    
    @Override
     public CopyOfBuilder phoneNumber(String phoneNumber) {
      return (CopyOfBuilder) super.phoneNumber(phoneNumber);
    }
    
    @Override
     public CopyOfBuilder address(String address) {
      return (CopyOfBuilder) super.address(address);
    }
    
    @Override
     public CopyOfBuilder status(JobStatus status) {
      return (CopyOfBuilder) super.status(status);
    }
  }
  
}
