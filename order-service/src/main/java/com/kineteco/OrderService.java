package com.kineteco;

import com.kineteco.model.KinetecoProductRanking;
import com.kineteco.model.ManufactureOrder;
import com.kineteco.model.ProductOrderStats;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderService {
   private static final Logger LOGGER = Logger.getLogger(OrderService.class);

   private final KinetecoProductRanking productRanking = new KinetecoProductRanking(3);

   /**
    * Multi Productos con las cantidades fabricar
    *
    * Resultado Multi de un iterable de estadísticas. SKU - Numero de Manufacture Orders
    *
    */
   public Multi<Iterable<ProductOrderStats>> computeProductsStats(Multi<ManufactureOrder> orders) {
      LOGGER.info("orders incoming");
      return transformManufactureOrderInOrderStats(orders);
   }

   private Multi<Iterable<ProductOrderStats>> transformManufactureOrderInOrderStats(Multi<ManufactureOrder> orders) {
      return orders
            .group().by(order -> order.sku)
            .onItem().transformToMultiAndMerge(group ->
                  group
                        .onItem().scan(ProductOrderStats::new, this::incrementScore))

            .onItem().transform(productRanking::onNewStat)
            .invoke(() -> LOGGER.info("Manufacture order received. Stats of top orders computed"));
   }

   private ProductOrderStats incrementScore(ProductOrderStats stats, ManufactureOrder order) {
      stats.sku = order.sku;
      stats.units++;
      return stats;
   }
}