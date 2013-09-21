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
  private AirStatus secondaryAirStatus;


  public boolean[] getSupportedPIDS() {
    return Arrays.copyOf(supportedPIDS, supportedPIDS.length);
  }


  public void setSupportedPIDS(final boolean[] supportedPIDS) {
    if (supportedPIDS == null) {
      this.supportedPIDS = null;
    } else {
      this.supportedPIDS = Arrays.copyOf(supportedPIDS, supportedPIDS.length);
    }
  }


  public MonitorStatus getMonitorStatus() {
    return monitorStatus;
  }


  public void setMonitorStatus(final MonitorStatus monitorStatus) {
    this.monitorStatus = monitorStatus;
  }


  public Integer getDistanceSinceErrorCodesCleared() {
    return distanceSinceErrorCodesCleared;
  }


  public void setDistanceSinceErrorCodesCleared(final Integer distanceSinceErrorCodesCleared) {
    this.distanceSinceErrorCodesCleared = distanceSinceErrorCodesCleared;
  }


  public Integer getDistanceWithMalfunction() {
    return distanceWithMalfunction;
  }


  public void setDistanceWithMalfunction(final Integer distanceWithMalfunction) {
    this.distanceWithMalfunction = distanceWithMalfunction;
  }


  public Integer getEngineCoolantTemperature() {
    return engineCoolantTemperature;
  }


  public void setEngineCoolantTemperature(final Integer engineCoolantTemperature) {
    this.engineCoolantTemperature = engineCoolantTemperature;
  }


  public Double getEngineLoad() {
    return engineLoad;
  }


  public void setEngineLoad(final Double engineLoad) {
    this.engineLoad = engineLoad;
  }


  public Double getFuelInjectionTiming() {
    return fuelInjectionTiming;
  }


  public void setFuelInjectionTiming(final Double fuelInjectionTiming) {
    this.fuelInjectionTiming = fuelInjectionTiming;
  }


  public Double getFuelLevelInput() {
    return fuelLevelInput;
  }


  public void setFuelLevelInput(final Double fuelLevelInput) {
    this.fuelLevelInput = fuelLevelInput;
  }


  public Double getFuelRate() {
    return fuelRate;
  }


  public void setFuelRate(final Double fuelRate) {
    this.fuelRate = fuelRate;
  }


  public FuelStatus getFuelStatus() {
    return fuelStatus;
  }


  public void setFuelStatus(final FuelStatus fuelStatus) {
    this.fuelStatus = fuelStatus;
  }


  public Double getFuelTrimPercentShortTerm() {
    return fuelTrimPercentShortTerm;
  }


  public void setFuelTrimPercentShortTerm(final Double fuelTrimPercentShortTerm) {
    this.fuelTrimPercentShortTerm = fuelTrimPercentShortTerm;
  }


  public Double getFuelTrimPercentLongTerm() {
    return fuelTrimPercentLongTerm;
  }


  public void setFuelTrimPercentLongTerm(final Double fuelTrimPercentLongTerm) {
    this.fuelTrimPercentLongTerm = fuelTrimPercentLongTerm;
  }


  public Integer getIntakeAirTemperature() {
    return intakeAirTemperature;
  }


  public void setIntakeAirTemperature(final Integer intakeAirTemperature) {
    this.intakeAirTemperature = intakeAirTemperature;
  }


  public void setFaults(final List<Fault> faults) {
    this.faults = faults;
  }


  public List<Fault> getFaults() {
    if (this.faults == null) {
      return null;
    }
    return Collections.unmodifiableList(this.faults);
  }


  public AirStatus getSecondaryAirStatus() {
    return secondaryAirStatus;
  }


  public void setSecondaryAirStatus(final AirStatus secondaryAirStatus) {
    this.secondaryAirStatus = secondaryAirStatus;
  }

}
