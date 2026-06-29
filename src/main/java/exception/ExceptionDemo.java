package exception;

import java.util.ArrayList;
import java.util.List;


//Mô phỏng xử lý lỗi JPA, CascadeType.PERSIST và Global Exception Handler

public class ExceptionDemo {

    public static void main(String[] args) {

        DailyReportRepository repository = new DailyReportRepository();

        SensorDataService service =
                new SensorDataService(repository);

        ReportRequest request =
                new ReportRequest(
                        "IOT-001",
                        29.5,
                        80
                );

        try {

            ReportResponse response =
                    service.createDailyReport(request);

            System.out.println(response);

        } catch (DatabaseException ex) {

            System.out.println(
                    GlobalExceptionHandler.handleDatabaseException(ex)
            );

        }

    }

}

 //Service
class SensorDataService {

    private final DailyReportRepository reportRepo;

    public SensorDataService(
            DailyReportRepository reportRepo) {

        this.reportRepo = reportRepo;
    }

    public ReportResponse createDailyReport(
            ReportRequest request) {

        System.out.println(
                "Đang xử lý dữ liệu từ thiết bị: "
                        + request.getDeviceCode());

        /*
         * Giả lập CascadeType.PERSIST
         */

        SensorDevice newSensor =
                new SensorDevice(
                        request.getDeviceCode(),
                        "ACTIVE"
                );

        DailyReport report =
                new DailyReport();

        report.setTemperature(
                request.getTemp());

        report.setHumidity(
                request.getHumidity());

        report.setSensorDevice(
                newSensor);

        reportRepo.save(report);

        return new ReportResponse(
                report.getId(),
                "SUCCESS"
        );

    }

}

 //Repository

class DailyReportRepository {

    private final List<DailyReport> database =
            new ArrayList<>();

    public void save(DailyReport report) {

         //Mô phỏng CascadeType.PERSIST

        if (report.getSensorDevice() == null) {

            throw new DatabaseException(
                    "Không thể lưu dữ liệu đồng bộ"
            );

        }

        report.setId(database.size() + 1L);

        database.add(report);

        System.out.println(
                "Đã lưu Report thành công."
        );

    }

}

 //Entity

class SensorDevice {

    private String deviceCode;

    private String status;

    public SensorDevice(
            String deviceCode,
            String status) {

        this.deviceCode = deviceCode;
        this.status = status;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

}

 //Entity

class DailyReport {

    private Long id;

    private double temperature;

    private int humidity;


    private SensorDevice sensorDevice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setSensorDevice(
            SensorDevice sensorDevice) {

        this.sensorDevice = sensorDevice;
    }

    public SensorDevice getSensorDevice() {
        return sensorDevice;
    }

}

 //DTO

class ReportRequest {

    private final String deviceCode;

    private final double temp;

    private final int humidity;

    public ReportRequest(
            String deviceCode,
            double temp,
            int humidity) {

        this.deviceCode = deviceCode;
        this.temp = temp;
        this.humidity = humidity;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public double getTemp() {
        return temp;
    }

    public int getHumidity() {
        return humidity;
    }

}

 //DTO

class ReportResponse {

    private final Long reportId;

    private final String status;

    public ReportResponse(
            Long reportId,
            String status) {

        this.reportId = reportId;
        this.status = status;
    }

    @Override
    public String toString() {

        return "ReportResponse{" +
                "reportId=" + reportId +
                ", status='" + status + '\'' +
                '}';

    }

}

 //Custom Exception

class DatabaseException
        extends RuntimeException {

    public DatabaseException(String message) {

        super(message);

    }

}

 //Global Exception Handler

class GlobalExceptionHandler {

    public static String handleDatabaseException(
            DatabaseException ex) {

        return """
                {
                  "error":"DATABASE_ERROR",
                  "message":"Không thể lưu dữ liệu đồng bộ"
                }
                """;

    }

}
