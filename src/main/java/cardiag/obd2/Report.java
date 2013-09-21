/**
 *
 */
package cardiag.obd2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author David Matějček
 */
public class Report {

  private boolean[] supportedPIDS;
  private MonitorStatus monitorStatus;
  private List<Fault> faults;
  private Integer distanceSinceErrorCodesCleared;
  private Integer distanceWithMalfunction;
  private Integer engineCoolantTemperature;
  private Double engineLoad;
  private Double fuelInjectionTiming;
  private Double fuelLevelInput;
  private Double fuelRate;
  private FuelStatus fuelStatus;
  private Double fuelTrimPercentShortTerm;
  private Double fuelTrimPercentLongTerm;
  private Integer intakeAirTemperature;


  public boolean[] getSupportedPIDS() {
    return Arrays.copyOf(supportedPIDS, supportedPIDS.length);
  }


  public void setSupportedPIDS(boolean[] supportedPIDS) {
    if (supportedPIDS == null) {
      this.supportedPIDS = null;
    } else {
      this.supportedPIDS = Arrays.copyOf(supportedPIDS, supportedPIDS.length);
    }
  }


  public MonitorStatus getMonitorStatus() {
    return monitorStatus;
  }


  public void setMonitorStatus(MonitorStatus monitorStatus) {
    this.monitorStatus = monitorStatus;
  }


  public Integer getDistanceSinceErrorCodesCleared() {
    return distanceSinceErrorCodesCleared;
  }


  public void setDistanceSinceErrorCodesCleared(Integer distanceSinceErrorCodesCleared) {
    this.distanceSinceErrorCodesCleared = distanceSinceErrorCodesCleared;
  }


  public Integer getDistanceWithMalfunction() {
    return distanceWithMalfunction;
  }


  public void setDistanceWithMalfunction(Integer distanceWithMalfunction) {
    this.distanceWithMalfunction = distanceWithMalfunction;
  }


  public Integer getEngineCoolantTemperature() {
    return engineCoolantTemperature;
  }


  public void setEngineCoolantTemperature(Integer engineCoolantTemperature) {
    this.engineCoolantTemperature = engineCoolantTemperature;
  }


  public Double getEngineLoad() {
    return engineLoad;
  }


  public void setEngineLoad(Double engineLoad) {
    this.engineLoad = engineLoad;
  }


  public Double getFuelInjectionTiming() {
    return fuelInjectionTiming;
  }


  public void setFuelInjectionTiming(Double fuelInjectionTiming) {
    this.fuelInjectionTiming = fuelInjectionTiming;
  }


  public Double getFuelLevelInput() {
    return fuelLevelInput;
  }


  public void setFuelLevelInput(Double fuelLevelInput) {
    this.fuelLevelInput = fuelLevelInput;
  }


  public Double getFuelRate() {
    return fuelRate;
  }


  public void setFuelRate(Double fuelRate) {
    this.fuelRate = fuelRate;
  }


  public FuelStatus getFuelStatus() {
    return fuelStatus;
  }


  public void setFuelStatus(FuelStatus fuelStatus) {
    this.fuelStatus = fuelStatus;
  }


  public Double getFuelTrimPercentShortTerm() {
    return fuelTrimPercentShortTerm;
  }


  public void setFuelTrimPercentShortTerm(Double fuelTrimPercentShortTerm) {
    this.fuelTrimPercentShortTerm = fuelTrimPercentShortTerm;
  }


  public Double getFuelTrimPercentLongTerm() {
    return fuelTrimPercentLongTerm;
  }


  public void setFuelTrimPercentLongTerm(Double fuelTrimPercentLongTerm) {
    this.fuelTrimPercentLongTerm = fuelTrimPercentLongTerm;
  }


  public Integer getIntakeAirTemperature() {
    return intakeAirTemperature;
  }


  public void setIntakeAirTemperature(Integer intakeAirTemperature) {
    this.intakeAirTemperature = intakeAirTemperature;
  }


  public void setFaults(List<Fault> faults) {
    this.faults = faults;
  }


  public List<Fault> getFaults() {
    if (this.faults == null) {
      return null;
    }
    return Collections.unmodifiableList(this.faults);
  }

}
