/*
 * Kubernetes
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v1.21.1
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package io.kubernetes.autoscaling.models;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;

/**
 * VerticalPodAutoscalerCondition describes the state of a VerticalPodAutoscaler at a certain point.
 */
@ApiModel(description = "VerticalPodAutoscalerCondition describes the state of a VerticalPodAutoscaler at a certain point.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-10-02T17:31:04.907Z[Etc/UTC]")
public class V1beta2VerticalPodAutoscalerStatusConditions {
  public static final String SERIALIZED_NAME_LAST_TRANSITION_TIME = "lastTransitionTime";
  @SerializedName(SERIALIZED_NAME_LAST_TRANSITION_TIME)
  private OffsetDateTime lastTransitionTime;

  public static final String SERIALIZED_NAME_MESSAGE = "message";
  @SerializedName(SERIALIZED_NAME_MESSAGE)
  private String message;

  public static final String SERIALIZED_NAME_REASON = "reason";
  @SerializedName(SERIALIZED_NAME_REASON)
  private String reason;

  public static final String SERIALIZED_NAME_STATUS = "status";
  @SerializedName(SERIALIZED_NAME_STATUS)
  private String status;

  public static final String SERIALIZED_NAME_TYPE = "type";
  @SerializedName(SERIALIZED_NAME_TYPE)
  private String type;


  public V1beta2VerticalPodAutoscalerStatusConditions lastTransitionTime(OffsetDateTime lastTransitionTime) {

    this.lastTransitionTime = lastTransitionTime;
    return this;
  }

   /**
   * lastTransitionTime is the last time the condition transitioned from one status to another
   * @return lastTransitionTime
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "lastTransitionTime is the last time the condition transitioned from one status to another")

  public OffsetDateTime getLastTransitionTime() {
    return lastTransitionTime;
  }


  public void setLastTransitionTime(OffsetDateTime lastTransitionTime) {
    this.lastTransitionTime = lastTransitionTime;
  }


  public V1beta2VerticalPodAutoscalerStatusConditions message(String message) {

    this.message = message;
    return this;
  }

   /**
   * message is a human-readable explanation containing details about the transition
   * @return message
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "message is a human-readable explanation containing details about the transition")

  public String getMessage() {
    return message;
  }


  public void setMessage(String message) {
    this.message = message;
  }


  public V1beta2VerticalPodAutoscalerStatusConditions reason(String reason) {

    this.reason = reason;
    return this;
  }

   /**
   * reason is the reason for the condition&#39;s last transition.
   * @return reason
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "reason is the reason for the condition's last transition.")

  public String getReason() {
    return reason;
  }


  public void setReason(String reason) {
    this.reason = reason;
  }


  public V1beta2VerticalPodAutoscalerStatusConditions status(String status) {

    this.status = status;
    return this;
  }

   /**
   * status is the status of the condition (True, False, Unknown)
   * @return status
  **/
  @ApiModelProperty(required = true, value = "status is the status of the condition (True, False, Unknown)")

  public String getStatus() {
    return status;
  }


  public void setStatus(String status) {
    this.status = status;
  }


  public V1beta2VerticalPodAutoscalerStatusConditions type(String type) {

    this.type = type;
    return this;
  }

   /**
   * type describes the current condition
   * @return type
  **/
  @ApiModelProperty(required = true, value = "type describes the current condition")

  public String getType() {
    return type;
  }


  public void setType(String type) {
    this.type = type;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    V1beta2VerticalPodAutoscalerStatusConditions v1beta2VerticalPodAutoscalerStatusConditions = (V1beta2VerticalPodAutoscalerStatusConditions) o;
    return Objects.equals(this.lastTransitionTime, v1beta2VerticalPodAutoscalerStatusConditions.lastTransitionTime) &&
        Objects.equals(this.message, v1beta2VerticalPodAutoscalerStatusConditions.message) &&
        Objects.equals(this.reason, v1beta2VerticalPodAutoscalerStatusConditions.reason) &&
        Objects.equals(this.status, v1beta2VerticalPodAutoscalerStatusConditions.status) &&
        Objects.equals(this.type, v1beta2VerticalPodAutoscalerStatusConditions.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lastTransitionTime, message, reason, status, type);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class V1beta2VerticalPodAutoscalerStatusConditions {\n");
    sb.append("    lastTransitionTime: ").append(toIndentedString(lastTransitionTime)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

