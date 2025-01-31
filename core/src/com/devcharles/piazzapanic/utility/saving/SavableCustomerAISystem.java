package com.devcharles.piazzapanic.utility.saving;

import com.badlogic.ashley.core.Entity;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Alistair Foggin
 */
public class SavableCustomerAISystem {

  public Map<Integer, Boolean> objectiveTaken;
  public SavableTimer spawnTimer;
  public int totalCustomers;
  public boolean firstSpawn = false;
  public int numQueuedCustomers = 0;
  public float patienceModifier = 1f;
  public int incomeModifier = 1;
  public ArrayList<ArrayList<SavableCustomer>> customers;

  /**
   * Copy the necessary state parameters from the actual system and create a savable version
   *
   * @param system the system to save
   * @return the savable system
   */
  public static SavableCustomerAISystem from(CustomerAISystem system) {
    if (system == null) {
      return null;
    }
    SavableCustomerAISystem savableSystem = new SavableCustomerAISystem();
    savableSystem.objectiveTaken = system.getObjectiveTaken();
    savableSystem.spawnTimer = SavableTimer.from(system.getSpawnTimer());
    savableSystem.totalCustomers = system.getTotalCustomers();
    savableSystem.firstSpawn = system.isFirstSpawn();
    savableSystem.numQueuedCustomers = system.getNumQueuedCustomers();
    savableSystem.patienceModifier = system.getPatienceModifier();
    savableSystem.incomeModifier = system.getIncomeModifier();

    ArrayList<ArrayList<Entity>> customers = system.getCustomerGroups();
    savableSystem.customers = new ArrayList<>(customers.size());
    for (ArrayList<Entity> group : customers) {
      ArrayList<SavableCustomer> savableGroup = new ArrayList<>(3);
      for (Entity customer : group) {
        savableGroup.add(SavableCustomer.from(customer));
      }
      savableSystem.customers.add(savableGroup);
    }
    return savableSystem;
  }
}
