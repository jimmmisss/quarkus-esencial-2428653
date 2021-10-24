package com.kineteco.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RegisterForReflection
@Entity
public class ProductInventory extends PanacheEntityBase {

   @Id
   @Null(groups = ValidationGroups.Put.class)
   @NotBlank(groups = ValidationGroups.Post.class)
   public String sku;

   public String category;

   @NotBlank(message = "Name is mandatory and should be provided")
   public String name;
   public int quantity = 0;
   public String powerWatts = "0";
   public String footprint ="0";
   public BigDecimal manufacturingCost;
   public BigDecimal price;
   @Enumerated(EnumType.STRING)
   public ProductLine productLine = ProductLine.ECONOMY;
   @Convert(converter = ConsumerTypeListConverter.class)
   public List<ConsumerType> targetConsumer = new ArrayList<>();
   @Enumerated(EnumType.STRING)
   public ProductAvailability productAvailability = ProductAvailability.ON_BACK_ORDER;

   @PositiveOrZero
   public int unitsAvailable = 0;

   public ProductInventory() {

   }

   public ProductInventory(String sku) {
      this.sku = sku;
   }

   public ProductInventory(String sku, String category, String name, int quantity, String powerWatts, String footprint,
                           BigDecimal manufacturingCost, BigDecimal price, ProductLine productLine,
                           List<ConsumerType> targetConsumer, ProductAvailability productAvailability, int unitsAvailable) {
      this.sku = sku;
      this.category = category;
      this.name = name;
      this.quantity = quantity;
      this.powerWatts = powerWatts;
      this.footprint = footprint;
      this.manufacturingCost = manufacturingCost;
      this.price = price;
      this.productLine = productLine;
      this.targetConsumer = targetConsumer;
      this.productAvailability = productAvailability;
      this.unitsAvailable = unitsAvailable;
   }

   @Override
   public String toString() {
      return "ProductInventory{" + "sku='" + sku + '\'' + ", category='" + category + '\'' + ", name='" + name + '\''
            + ", quantity=" + quantity + ", powerWatts=" + powerWatts + ", footprint=" + footprint
            + ", manufacturingCost=" + manufacturingCost + ", price=" + price + ", productLine=" + productLine
            + ", targetConsumer=" + targetConsumer.toString() + ", productAvailability=" + productAvailability
            + ", unitsAvailable=" + unitsAvailable + '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ProductInventory that = (ProductInventory) o;
      return quantity == that.quantity && Objects.equals(powerWatts, that.powerWatts) && Objects
            .equals(footprint, that.footprint) && unitsAvailable == that.unitsAvailable && Objects.equals(sku, that.sku)
            && Objects.equals(category, that.category) && Objects.equals(name, that.name) && Objects
            .equals(manufacturingCost, that.manufacturingCost) && Objects.equals(price, that.price)
            && productLine == that.productLine && Objects.equals(targetConsumer, that.targetConsumer)
            && productAvailability == that.productAvailability;
   }

   @Override
   public int hashCode() {
      return Objects
            .hash(sku, category, name, quantity, powerWatts, footprint, manufacturingCost, price, productLine,
                  targetConsumer, productAvailability, unitsAvailable);
   }
}
