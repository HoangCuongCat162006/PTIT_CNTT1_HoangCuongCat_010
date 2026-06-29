package farm;


import java.util.HashMap;
import java.util.Map;

public class FarmMaintenanceService {

    private final MaintenanceStrategyFactory factory;

    public FarmMaintenanceService() {

        factory = new MaintenanceStrategyFactory();

        factory.registerStrategy(new RiceTerracesStrategy());
        factory.registerStrategy(new StrawberryGreenhouseStrategy());
        factory.registerStrategy(new FruitOrchardStrategy());

        // Thêm loại mới
        factory.registerStrategy(new GinsengValleyStrategy());
    }

    public Invoice calculateMaintenanceCost(FarmArea farm,
                                            int durationInMonths) {

        System.out.println(
                "Bắt đầu tính toán chi phí bảo trì hệ thống cho khu vực: "
                        + farm.getName());

        MaintenanceStrategy strategy =
                factory.getStrategy(farm.getTerrainType());

        double baseCost =
                strategy.calculateCost(durationInMonths);

        return new Invoice(
                farm.getId(),
                baseCost,
                "CALCULATED"
        );
    }

    // Test

    public static void main(String[] args) {

        FarmMaintenanceService service =
                new FarmMaintenanceService();

        FarmArea farm =
                new FarmArea(
                        1L,
                        "Trang trại Tây Bắc",
                        "RICE_TERRACES"
                );

        Invoice invoice =
                service.calculateMaintenanceCost(
                        farm,
                        6
                );

        System.out.println(invoice);
    }

    // Strategy

    interface MaintenanceStrategy {

        String getType();

        double calculateCost(int durationInMonths);

    }

    static class RiceTerracesStrategy
            implements MaintenanceStrategy {

        @Override
        public String getType() {
            return "RICE_TERRACES";
        }

        @Override
        public double calculateCost(int durationInMonths) {

            System.out.println(
                    "Đang tính phụ phí bảo trì trạm bơm áp suất cao..."
            );

            return durationInMonths * 2500000.0;
        }
    }

    static class StrawberryGreenhouseStrategy
            implements MaintenanceStrategy {

        @Override
        public String getType() {
            return "STRAWBERRY_GREENHOUSE";
        }

        @Override
        public double calculateCost(int durationInMonths) {

            System.out.println(
                    "Đang tính chi phí hiệu chuẩn cảm biến..."
            );

            return durationInMonths * 4000000.0;
        }
    }

    static class FruitOrchardStrategy
            implements MaintenanceStrategy {

        @Override
        public String getType() {
            return "FRUIT_ORCHARD";
        }

        @Override
        public double calculateCost(int durationInMonths) {

            System.out.println(
                    "Đang tính chi phí kiểm tra hệ thống tưới..."
            );

            return durationInMonths * 1000000.0;
        }
    }

    static class GinsengValleyStrategy
            implements MaintenanceStrategy {

        @Override
        public String getType() {
            return "GINSENG_VALLEY";
        }

        @Override
        public double calculateCost(int durationInMonths) {

            System.out.println(
                    "Đang tính chi phí bảo trì IoT cho vườn sâm..."
            );

            return durationInMonths * 5500000.0;
        }
    }

    // Factory

    static class MaintenanceStrategyFactory {

        private final Map<String, MaintenanceStrategy> strategyMap =
                new HashMap<>();

        public void registerStrategy(
                MaintenanceStrategy strategy) {

            strategyMap.put(
                    strategy.getType(),
                    strategy
            );
        }

        public MaintenanceStrategy getStrategy(
                String type) {

            MaintenanceStrategy strategy =
                    strategyMap.get(type);

            if (strategy == null) {

                throw new IllegalArgumentException(
                        "Loại hình canh tác chưa được hỗ trợ."
                );
            }

            return strategy;
        }

    }

}
class FarmArea {

    private Long id;

    private String name;

    private String terrainType;

    public FarmArea(Long id,
                    String name,
                    String terrainType) {

        this.id = id;
        this.name = name;
        this.terrainType = terrainType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTerrainType() {
        return terrainType;
    }

}

class Invoice {

    private Long farmId;

    private double amount;

    private String status;

    public Invoice(Long farmId,
                   double amount,
                   String status) {

        this.farmId = farmId;
        this.amount = amount;
        this.status = status;
    }

    @Override
    public String toString() {

        return "Invoice{" +
                "farmId=" + farmId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }

}
