package com.kineteco.service;

import com.kineteco.config.ProductInventoryConfig;
import com.kineteco.model.ConsumerType;
import com.kineteco.model.ProductAvailability;
import com.kineteco.model.ProductInventory;
import com.kineteco.model.ProductLine;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ProductInventoryService {
   private Map<String, ProductInventory> inventory = new HashMap();

   @Inject
   ProductInventoryConfig productInventoryConfig;

   void onStart(@Observes StartupEvent ev) {
      System.out.println("  _   _   _   _   _   _   _   _");
      System.out.println(" / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\");
      System.out.println("( K | i | n | e | t | e | c | o )");
      System.out.println(" \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/");
      loadData();
   }

   void loadData() {
      inventory.clear();
      InputStream resourceAsStream = this.getClass().getClassLoader()
            .getResourceAsStream("KinetEco_product_inventory.csv");
      try {
         try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            String line;
            int id = 0;
            // Category,Name,Package Quantity,SKU,Power (Watts),Footprint (SQ FT),Manufacturing Cost,Suggested Retail,Product Line,Target Consumer,Availability,Units Available
            while ((line = br.readLine()) != null) {
               String[] values = line.split(",");
               ProductInventory productInventory = new ProductInventory();
               productInventory.setCategory(values[0]);
               productInventory.setName(values[1]);
               productInventory.setQuantity(Integer.parseInt(values[2]));
               productInventory.setSku(values[3]);
               productInventory.setPowerWatts(values[4]);
               productInventory.setFootprint(values[5]);
               productInventory.setManufacturingCost(new BigDecimal(values[6].replace("$","")));
               productInventory.setPrice(new BigDecimal(values[7].replace("$","")));
               productInventory.setProductLine(ProductLine.valueOf(values[8].toUpperCase()));
               List<ConsumerType> targetConsumers = new ArrayList<>();
               String toUpperCaseConsumerTypes = values[9].toUpperCase();
               for( ConsumerType consumerType: ConsumerType.values()) {
                  parseConsumerTypes(toUpperCaseConsumerTypes, consumerType, targetConsumers);
               }
               productInventory.setTargetConsumer(targetConsumers);
               productInventory.setProductAvailability(ProductAvailability.valueOf(values[10].replace(" ", "_").toUpperCase()));
               productInventory.setUnitsAvailable(Integer.parseInt(values[11]));

               if (productInventoryConfig.fullCatalog() || productInventory.getTargetConsumer().contains(ConsumerType.CORPORATE)){
                  inventory.put(productInventory.getSku(), productInventory);
               }

               id++;
            }
         }
      } catch (Exception e) {
         System.out.println(e);
      }
      System.out.println("===========> loaded " + inventory.size());
   }

   private void parseConsumerTypes(String values, ConsumerType consumerType, List<ConsumerType> targetConsumers) {
      if (values.contains(consumerType.name())) {
         targetConsumers.add(consumerType);
      }
   }

   void onStop(@Observes ShutdownEvent ev) {
      System.out.println("===========> onStop");
   }

   public ProductInventory getBySku(String sku) {
      return inventory.get(sku);
   }
}
