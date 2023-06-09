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

/**
 * Specification of the behavior of the autoscaler. More info: https://git.k8s.io/community/contributors/devel/api-conventions.md#spec-and-status.
 */
@ApiModel(description = "Specification of the behavior of the autoscaler. More info: https://git.k8s.io/community/contributors/devel/api-conventions.md#spec-and-status.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-10-02T17:31:04.907Z[Etc/UTC]")
public class V1beta2VerticalPodAutoscalerSpec {
  public static final String SERIALIZED_NAME_RESOURCE_POLICY = "resourcePolicy";
  @SerializedName(SERIALIZED_NAME_RESOURCE_POLICY)
  private V1beta2VerticalPodAutoscalerSpecResourcePolicy resourcePolicy;

  public static final String SERIALIZED_NAME_TARGET_REF = "targetRef";
  @SerializedName(SERIALIZED_NAME_TARGET_REF)
  private V1beta2VerticalPodAutoscalerSpecTargetRef targetRef;

  public static final String SERIALIZED_NAME_UPDATE_POLICY = "updatePolicy";
  @SerializedName(SERIALIZED_NAME_UPDATE_POLICY)
  private V1beta2VerticalPodAutoscalerSpecUpdatePolicy updatePolicy;


  public V1beta2VerticalPodAutoscalerSpec resourcePolicy(V1beta2VerticalPodAutoscalerSpecResourcePolicy resourcePolicy) {

    this.resourcePolicy = resourcePolicy;
    return this;
  }

   /**
   * Get resourcePolicy
   * @return resourcePolicy
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public V1beta2VerticalPodAutoscalerSpecResourcePolicy getResourcePolicy() {
    return resourcePolicy;
  }


  public void setResourcePolicy(V1beta2VerticalPodAutoscalerSpecResourcePolicy resourcePolicy) {
    this.resourcePolicy = resourcePolicy;
  }


  public V1beta2VerticalPodAutoscalerSpec targetRef(V1beta2VerticalPodAutoscalerSpecTargetRef targetRef) {

    this.targetRef = targetRef;
    return this;
  }

   /**
   * Get targetRef
   * @return targetRef
  **/
  @ApiModelProperty(required = true, value = "")

  public V1beta2VerticalPodAutoscalerSpecTargetRef getTargetRef() {
    return targetRef;
  }


  public void setTargetRef(V1beta2VerticalPodAutoscalerSpecTargetRef targetRef) {
    this.targetRef = targetRef;
  }


  public V1beta2VerticalPodAutoscalerSpec updatePolicy(V1beta2VerticalPodAutoscalerSpecUpdatePolicy updatePolicy) {

    this.updatePolicy = updatePolicy;
    return this;
  }

   /**
   * Get updatePolicy
   * @return updatePolicy
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public V1beta2VerticalPodAutoscalerSpecUpdatePolicy getUpdatePolicy() {
    return updatePolicy;
  }


  public void setUpdatePolicy(V1beta2VerticalPodAutoscalerSpecUpdatePolicy updatePolicy) {
    this.updatePolicy = updatePolicy;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    V1beta2VerticalPodAutoscalerSpec v1beta2VerticalPodAutoscalerSpec = (V1beta2VerticalPodAutoscalerSpec) o;
    return Objects.equals(this.resourcePolicy, v1beta2VerticalPodAutoscalerSpec.resourcePolicy) &&
        Objects.equals(this.targetRef, v1beta2VerticalPodAutoscalerSpec.targetRef) &&
        Objects.equals(this.updatePolicy, v1beta2VerticalPodAutoscalerSpec.updatePolicy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourcePolicy, targetRef, updatePolicy);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class V1beta2VerticalPodAutoscalerSpec {\n");
    sb.append("    resourcePolicy: ").append(toIndentedString(resourcePolicy)).append("\n");
    sb.append("    targetRef: ").append(toIndentedString(targetRef)).append("\n");
    sb.append("    updatePolicy: ").append(toIndentedString(updatePolicy)).append("\n");
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

