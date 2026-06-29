# Báo cáo & Lịch sử Prompt

# PHẦN 1: TÁI CẤU TRÚC HỆ THỐNG ĐỂ DỄ MỞ RỘNG

## 1. Mục tiêu kỹ thuật

### Bài toán

Hệ thống tính toán chi phí bảo trì cho nhiều loại địa hình canh tác đang sử dụng nhiều câu lệnh `if-else`. Khi xuất hiện loại hình canh tác mới, lập trình viên phải sửa trực tiếp hàm `calculateMaintenanceCost()`, làm vi phạm nguyên tắc Open/Closed Principle (OCP).

### Giải pháp lựa chọn

Áp dụng **Strategy Pattern** kết hợp với **Factory Pattern**.

Lý do:

* Mỗi loại hình canh tác sẽ có một Strategy riêng.
* FarmMaintenanceService chỉ làm nhiệm vụ điều phối.
* Khi thêm loại hình mới chỉ cần tạo thêm Strategy mới.
* Không sửa mã nguồn cũ của FarmMaintenanceService.
* Tuân thủ nguyên tắc Open/Closed Principle.

---

# 2. Prompt Chain

## Prompt 1

```text
Bạn là Senior Java Software Architect.

Hãy đọc đoạn code dưới đây.

Phân tích xem đoạn code đang vi phạm những nguyên lý nào của SOLID.

Không viết code ngay.

Chỉ phân tích nguyên nhân gây khó mở rộng và những rủi ro khi hệ thống có thêm nhiều loại Farm mới.
```

---

### AI trả lời

```text
Đoạn code đang vi phạm Open/Closed Principle (OCP).

Nguyên nhân:

- Hàm calculateMaintenanceCost() chứa toàn bộ logic xử lý.
- Sử dụng nhiều if-else theo loại địa hình.
- Mỗi khi xuất hiện Farm mới phải sửa lại source code.

Ngoài ra còn có dấu hiệu vi phạm Single Responsibility Principle vì Service vừa điều phối vừa xử lý nghiệp vụ.

Nếu số lượng Farm tăng lên 20 hoặc 30 loại thì phương thức sẽ rất dài, khó kiểm thử và khó bảo trì.
```

---

## Prompt 2

```text
Tiếp tục bài toán trên.

Hãy đề xuất Design Pattern phù hợp nhất để loại bỏ toàn bộ chuỗi if-else.

So sánh ngắn giữa Strategy Pattern và Factory Method.

Sau đó đề xuất kiến trúc tối ưu cho hệ thống Smart Agriculture.
```

---

### AI trả lời

```text
Strategy Pattern phù hợp nhất.

Mỗi loại Farm sẽ có một chiến lược tính toán riêng.

Factory sẽ chịu trách nhiệm lựa chọn Strategy tương ứng.

FarmMaintenanceService chỉ gọi Strategy mà không cần biết chi tiết xử lý.

Kiến trúc:

FarmMaintenanceService
        ↓
MaintenanceStrategyFactory
        ↓
MaintenanceStrategy
      /      |       \
RiceStrategy
FruitStrategy
GreenHouseStrategy
```

---

## Prompt 3

```text
Dựa trên kiến trúc vừa đề xuất.

Hãy sinh đầy đủ source code Java.

Yêu cầu:

- Interface MaintenanceStrategy
- Strategy cho từng loại Farm
- Factory chọn Strategy
- FarmMaintenanceService mới
- Không dùng if-else trong FarmMaintenanceService
- Có ví dụ mở rộng thêm GINSENG_VALLEY chỉ bằng cách tạo class mới.
```

---

### AI trả lời

AI sinh mã nguồn theo Strategy Pattern.

Tuy nhiên Factory vẫn sử dụng nhiều câu lệnh if-else để lựa chọn Strategy.

Điều này khiến hệ thống vẫn chưa thật sự tuân thủ OCP vì mỗi khi thêm Farm mới vẫn phải sửa Factory.

---

# 3. Phân tích lỗi AI

### Điểm AI làm chưa tối ưu

AI đã loại bỏ được if-else trong FarmMaintenanceService nhưng lại chuyển toàn bộ if-else sang Factory.

Ví dụ:

```java
if(type.equals("RICE")) ...
else if(...)
```

Khi thêm Farm mới vẫn phải sửa Factory.

Như vậy chỉ chuyển vị trí vi phạm OCP chứ chưa giải quyết tận gốc.

---

## Prompt 4 (Cải thiện)

```text
Không sử dụng if-else hoặc switch trong Factory.

Hãy thiết kế Factory sử dụng Map<String, MaintenanceStrategy>.

Mỗi Strategy tự đăng ký vào Factory.

Khi bổ sung Farm mới chỉ tạo class mới, không sửa Factory cũng như FarmMaintenanceService.
```

---

### AI trả lời

AI đề xuất Factory quản lý bằng Map.

Các Strategy được đăng ký theo key.

Factory chỉ lấy Strategy từ Map.

FarmMaintenanceService không cần thay đổi.

Giải pháp này tuân thủ hoàn toàn Open/Closed Principle.

---

# 4. Nhận xét

Sau khi cải thiện Prompt, kiến trúc hệ thống đạt được các mục tiêu:

* Tuân thủ OCP.
* Giảm phụ thuộc giữa Service và từng loại Farm.
* Dễ Unit Test.
* Dễ mở rộng.
* Có thể bổ sung loại hình GINSENG_VALLEY chỉ bằng cách tạo thêm Strategy mới.

Đây là kiến trúc phù hợp cho hệ thống Smart Agriculture có khả năng mở rộng lâu dài.


# PHẦN 2: DEBUGGING BẢO MẬT VÀ XỬ LÝ LỖI HỆ THỐNG

## 1. Mục tiêu kỹ thuật

### Bài toán

Hệ thống Spring Boot sử dụng JPA và MySQL gặp lỗi:

```
org.hibernate.TransientPropertyValueException
```

khi lưu DailyReport có quan hệ ManyToOne với SensorDevice.

Nguyên nhân là SensorDevice vừa được khởi tạo nhưng chưa được persist xuống database.

### Mục tiêu

* Điều tra nguyên nhân theo Entity Lifecycle.
* Phân tích Transient, Managed, Detached.
* Khắc phục bằng CascadeType.PERSIST.
* Hoặc sửa logic Service.
* Thêm Global Exception Handler bằng @RestControllerAdvice.
* Client luôn nhận JSON thay vì HTTP 500.

---

# 2. Prompt Chain

## Prompt 1

```text
Bạn là Senior Java Backend Developer.

Hãy phân tích nguyên nhân của lỗi:

org.hibernate.TransientPropertyValueException

Giải thích theo Entity Lifecycle của Hibernate.

Không viết code.

Chỉ phân tích Transient, Managed, Detached và Persistent.
```

---

### AI trả lời

```text
Entity vừa new sẽ ở trạng thái Transient.

Hibernate không quản lý Entity này.

Khi DailyReport được persist nhưng SensorDevice vẫn là Transient thì Hibernate không biết phải lưu SensorDevice trước hay không.

Do đó phát sinh TransientPropertyValueException.
```

---

## Prompt 2

```text
Từ phân tích trên.

Hãy đề xuất các cách sửa.

So sánh:

- CascadeType.PERSIST

- CascadeType.ALL

- Persist Sensor trước rồi mới persist Report.

Nêu ưu nhược điểm từng cách.
```

---

### AI trả lời

```text
Có ba cách.

1. CascadeType.PERSIST

Hibernate sẽ tự persist Sensor trước khi persist Report.

2. CascadeType.ALL

Cascade toàn bộ thao tác Persist, Merge, Remove...

3. Persist SensorDevice trước rồi mới gán vào Report.

Đối với bài toán này CascadeType.PERSIST là phù hợp nhất.
```

---

## Prompt 3

```text
Viết lại source code.

Yêu cầu:

- Thêm CascadeType.PERSIST.

- Thêm GlobalExceptionHandler.

- Khi có lỗi Database phải trả về:

{
 "error":"DATABASE_ERROR",
 "message":"Không thể lưu dữ liệu đồng bộ"
}
```

---

### AI trả lời

AI sinh được Entity có CascadeType.PERSIST.

Tuy nhiên Exception Handler chỉ bắt Exception chung:

```java
catch(Exception e)
```

Điều này chưa đúng yêu cầu vì đề yêu cầu xử lý lỗi JPA.

---

# 3. Phân tích lỗi AI

### Điểm AI chưa tối ưu

AI sử dụng:

```java
@ExceptionHandler(Exception.class)
```

để xử lý toàn bộ lỗi.

Cách này quá rộng.

Không thể hiện được đây là lỗi truy cập Database.

---

## Prompt 4 (Cải thiện)

```text
Chỉ xử lý lỗi liên quan đến JPA.

Sử dụng DataAccessException hoặc PersistenceException.

Không dùng Exception chung.

Trả về JSON đúng yêu cầu đề bài.
```

---

### AI trả lời

AI sửa lại GlobalExceptionHandler sử dụng DataAccessException.

Đây là cách phù hợp hơn vì chỉ bắt các lỗi truy cập Database.

---

# 4. Ý nghĩa của CascadeType.PERSIST

CascadeType.PERSIST giúp Hibernate tự động lưu Entity liên kết khi Entity cha được persist.

Trong bài toán này:

DailyReport là Entity cha.

SensorDevice là Entity liên kết.

Khi gọi:

reportRepository.save(report);

Hibernate sẽ tự động persist SensorDevice trước rồi mới persist DailyReport.

Nhờ đó tránh được lỗi:

TransientPropertyValueException.

---

# 5. Nhận xét

Sau khi cải thiện Prompt:

* Xử lý đúng Entity Lifecycle.
* Không còn lỗi HTTP 500.
* Client luôn nhận JSON.
* Hệ thống ổn định hơn.
* Tuân thủ đúng yêu cầu của đề bài.



# PHẦN 3 - NHIỆM VỤ 1: ĐỀ XUẤT GIẢI PHÁP CÔNG NGHỆ (TECH STACK)

# 1. Mục tiêu kỹ thuật

Với vai trò Chuyên viên phân tích hệ thống (System Analyst), sử dụng AI để đề xuất một bộ công nghệ phù hợp cho hệ thống **AgriSmart Market**.

Hệ thống cần đáp ứng các yêu cầu:

* Quản lý người dùng theo Role (Farmer, Buyer, Admin).
* Quản lý nông sản và chứng nhận theo mô hình dữ liệu chuẩn hóa.
* Xử lý thống kê bằng Java 8 Stream API.
* Gọi API của dịch vụ Logistics bên ngoài.
* Phát triển theo kiến trúc Monolithic Java Web.

---

# 2. Prompt Chain

## Prompt 1

```text
Bạn là Senior Solution Architect có hơn 15 năm kinh nghiệm phát triển hệ thống Java Enterprise.

Tôi đang xây dựng hệ thống "AgriSmart Market" là ứng dụng Monolithic Java Web.

Hệ thống có các yêu cầu:

- Quản lý Farmer, Buyer, Admin.
- Quản lý Batch và nhiều loại chứng nhận.
- Tổng hợp sản lượng bằng Java Stream API.
- Gọi REST API sang hệ thống Logistics.

Hãy đề xuất một Tech Stack phù hợp.

Bao gồm:

- Backend
- Frontend
- Database
- ORM
- Authentication
- API Communication
- Build Tool
- Testing

Giải thích lý do lựa chọn từng công nghệ.
```

---

### AI trả lời

AI đề xuất bộ công nghệ:

| Thành phần     | Công nghệ                   |
| -------------- | --------------------------- |
| Backend        | Spring Boot                 |
| Frontend       | Thymeleaf                   |
| Database       | MySQL                       |
| ORM            | Spring Data JPA + Hibernate |
| Authentication | Spring Security             |
| Build Tool     | Maven                       |
| API            | RestTemplate hoặc WebClient |
| Testing        | JUnit + Mockito             |

---

## Prompt 2

```text
Dựa trên Tech Stack trên.

Hãy giải thích vì sao bộ công nghệ này phù hợp với yêu cầu của khách hàng.

Đặc biệt phân tích:

- Vì sao MySQL phù hợp với dữ liệu chuẩn hóa.

- Vì sao Spring Data JPA phù hợp với nhiều quan hệ Entity.

- Vì sao Java Stream phù hợp cho xử lý thống kê.

- Vì sao REST API phù hợp khi kết nối hệ thống Logistics.
```

---

### AI trả lời

AI giải thích:

* MySQL hỗ trợ chuẩn hóa dữ liệu rất tốt, phù hợp với quan hệ nhiều-nhiều giữa Batch và Certification.

* Spring Data JPA giúp ánh xạ Entity đơn giản, hỗ trợ OneToMany và ManyToMany.

* Java Stream giúp xử lý groupBy, filtering và aggregation trên Collection hiệu quả, giảm mã lặp.

* REST API là chuẩn giao tiếp phổ biến giữa các hệ thống, dễ tích hợp với dịch vụ Logistics bên ngoài.

---

# 3. Tóm tắt giải pháp công nghệ

### Backend

* Spring Boot

Lý do:

* Phát triển nhanh.
* Hệ sinh thái mạnh.
* Dễ tích hợp Spring Security và Spring Data JPA.

---

### Frontend

* Thymeleaf

Lý do:

* Phù hợp ứng dụng Monolithic.
* Render trực tiếp từ phía Server.
* Tích hợp tốt với Spring Boot.

---

### Database

* MySQL

Lý do:

* Hỗ trợ mô hình dữ liệu quan hệ.
* Dễ chuẩn hóa dữ liệu.
* Phù hợp thiết kế nhiều bảng liên kết.

---

### ORM

* Spring Data JPA + Hibernate

Lý do:

* Hỗ trợ ánh xạ Entity.
* Quản lý OneToMany, ManyToMany.
* Giảm lượng SQL thủ công.

---

### Authentication

* Spring Security

Lý do:

* Quản lý Role Farmer, Buyer và Admin.
* Phân quyền dễ dàng.

---

### API Communication

* RestTemplate hoặc WebClient

Lý do:

* Dễ gọi REST API của hệ thống Logistics.
* Hỗ trợ trao đổi dữ liệu JSON.

---

### Build Tool

* Maven

Lý do:

* Quản lý dependency hiệu quả.
* Chuẩn trong các dự án Spring Boot.

---

### Testing

* JUnit và Mockito

Lý do:

* Kiểm thử Unit Test.
* Mock Repository và Service dễ dàng.

---

# 4. Lý do thuyết phục khách hàng

Giải pháp công nghệ trên phù hợp với yêu cầu của dự án vì:

* Phù hợp với kiến trúc Monolithic Java Web mà khách hàng yêu cầu.
* Hỗ trợ tốt cho quản lý dữ liệu quan hệ và chuẩn hóa cơ sở dữ liệu.
* Dễ mở rộng khi bổ sung thêm nghiệp vụ hoặc vai trò người dùng.
* Hệ sinh thái Spring giúp phát triển nhanh, bảo trì thuận tiện và có cộng đồng hỗ trợ lớn.
* Việc sử dụng REST API giúp tích hợp với hệ thống Logistics linh hoạt và dễ thay đổi trong tương lai.

---

# 5. Nhận xét phản biện

Em **đồng ý** với đề xuất của AI.

Lý do:

* Bộ công nghệ phù hợp với yêu cầu của dự án và đúng với kiến trúc Monolithic Java Web.
* Spring Boot kết hợp Spring Data JPA và MySQL là lựa chọn phổ biến, ổn định và dễ triển khai.
* Java Stream đáp ứng đúng yêu cầu xử lý thống kê mà đề bài đặt ra.
* Spring Security đáp ứng tốt việc phân quyền theo ba vai trò Farmer, Buyer và Admin.
* REST API là lựa chọn hợp lý để kết nối với hệ thống Logistics.

Tuy nhiên, em đề xuất ưu tiên **Spring WebClient** thay cho **RestTemplate** nếu dự án phát triển lâu dài, vì WebClient là công nghệ mới hơn, hỗ trợ lập trình bất đồng bộ (Reactive), hiệu quả hơn khi tích hợp với nhiều dịch vụ bên ngoài. Trong trường hợp dự án chỉ ở quy mô vừa và vẫn sử dụng kiến trúc Monolithic truyền thống thì RestTemplate vẫn là một lựa chọn đơn giản và phù hợp.





# PHẦN 3 - NHIỆM VỤ 2: PHÂN TÍCH THỰC THỂ (ENTITY ANALYSIS)

# 1. Mục tiêu kỹ thuật

Phân tích nghiệp vụ của hệ thống **AgriSmart Market** để xác định các thực thể (Entities) cốt lõi trong cơ sở dữ liệu.

Đảm bảo:

* Thiết kế cơ sở dữ liệu theo mô hình quan hệ.
* Chuẩn hóa dữ liệu.
* Không sử dụng mảng (Array) hoặc chuỗi JSON để lưu danh sách chứng nhận.
* Thiết kế quan hệ phù hợp để thương lái có thể tìm kiếm động theo từng loại chứng nhận.

---

# 2. Prompt Chain

## Prompt 1

```text id="1m8vzc"
Bạn là System Analyst có kinh nghiệm thiết kế hệ thống Java Web.

Dựa trên mô tả nghiệp vụ sau:

- Quản lý Farmer, Buyer và Admin.
- Quản lý các lô nông sản (Batch).
- Mỗi Batch có thể đạt nhiều chứng nhận như VietGAP, GlobalGAP, OCOP...
- Không được lưu danh sách chứng nhận bằng Array hoặc JSON.
- Thương lái cần tìm kiếm Batch theo từng loại chứng nhận.

Hãy phân tích các Entity cốt lõi của Database.

Chỉ liệt kê Entity và mô tả ngắn chức năng của từng Entity.
```

---

### AI trả lời

AI đề xuất các thực thể:

* User
* Role
* Farm
* ProductBatch
* Certification
* Order
* LogisticsRequest

---

## Prompt 2

```text id="0ojd04"
Tiếp tục phân tích.

Hãy xác định quan hệ giữa các Entity.

Đặc biệt giải thích cách chuẩn hóa dữ liệu cho ProductBatch và Certification.

Không sử dụng Array hoặc JSON để lưu nhiều chứng nhận.
```

---

### AI trả lời

AI phân tích:

* Một Farmer quản lý nhiều Farm.
* Một Farm có nhiều ProductBatch.
* Một ProductBatch có nhiều Certification.
* Một Certification cũng có thể áp dụng cho nhiều ProductBatch.

Do đó nên thiết kế quan hệ **Many-to-Many** thông qua bảng trung gian **BatchCertification** để đảm bảo chuẩn hóa dữ liệu và hỗ trợ tìm kiếm theo từng loại chứng nhận.

---

# 3. Danh sách Entities

| Entity                 | Mô tả                                                                             |
| ---------------------- | --------------------------------------------------------------------------------- |
| **User**               | Lưu thông tin người dùng đăng nhập hệ thống.                                      |
| **Role**               | Quản lý quyền của người dùng (Farmer, Buyer, Admin).                              |
| **Farm**               | Lưu thông tin trang trại thuộc Farmer.                                            |
| **ProductBatch**       | Lưu thông tin từng lô nông sản như tên, loại, sản lượng dự kiến, trạng thái.      |
| **Certification**      | Lưu danh mục các chứng nhận (VietGAP, GlobalGAP, OCOP...).                        |
| **BatchCertification** | Bảng trung gian liên kết ProductBatch và Certification theo quan hệ Many-to-Many. |
| **Order**              | Lưu thông tin đơn đặt mua của Buyer đối với ProductBatch.                         |
| **LogisticsRequest**   | Lưu thông tin yêu cầu gửi sang hệ thống Logistics để tìm xe vận chuyển.           |

---

# 4. Phân tích quan hệ

* **Role (1) ---- (N) User**

Một Role có thể được gán cho nhiều User.

---

* **User (Farmer) (1) ---- (N) Farm**

Một Farmer có thể sở hữu nhiều trang trại.

---

* **Farm (1) ---- (N) ProductBatch**

Một trang trại có thể có nhiều lô nông sản.

---

* **ProductBatch (N) ---- (N) Certification**

Quan hệ nhiều-nhiều.

Được chuẩn hóa thông qua bảng:

**BatchCertification**

Ví dụ:

| Batch   | Certification |
| ------- | ------------- |
| Batch A | VietGAP       |
| Batch A | GlobalGAP     |
| Batch B | OCOP          |

Thiết kế này giúp:

* Không lưu nhiều chứng nhận trong một cột.
* Không dùng Array.
* Không dùng JSON.
* Hỗ trợ tìm kiếm theo từng loại chứng nhận bằng JOIN.

---

* **Buyer (User) (1) ---- (N) Order**

Một Buyer có thể tạo nhiều đơn hàng.

---

* **Order (1) ---- (1) LogisticsRequest**

Sau khi đơn hàng được xác nhận, hệ thống tạo yêu cầu gửi sang dịch vụ Logistics để tìm xe vận chuyển.

---

# 5. Nhận xét

Em **đồng ý** với kết quả phân tích của AI.

Lý do:

* Các Entity phản ánh đầy đủ nghiệp vụ của hệ thống.
* Thiết kế cơ sở dữ liệu tuân thủ nguyên tắc chuẩn hóa.
* Quan hệ Many-to-Many giữa **ProductBatch** và **Certification** thông qua bảng **BatchCertification** đáp ứng đúng yêu cầu của đề bài, giúp hệ thống dễ dàng mở rộng và hỗ trợ truy vấn theo từng loại chứng nhận mà không cần sử dụng Array hoặc chuỗi JSON.





# PHẦN 3 – NHIỆM VỤ 3: THIẾT KẾ SƠ ĐỒ ERD

## Prompt 1

Tôi đang xây dựng hệ thống **AgriSmart Market** (Monolithic Java Web) cho liên minh Hợp tác xã Nông nghiệp.

Hãy phân tích các thực thể sau và xác định đầy đủ các quan hệ giữa chúng:

* User (Farmer, Buyer, Admin)
* Farm
* CropType
* ProductBatch
* Certification
* BatchCertification
* Order
* LogisticsRequest

Yêu cầu:

* Chuẩn hóa cơ sở dữ liệu theo chuẩn 3NF.
* Không sử dụng mảng (Array) hoặc chuỗi JSON để lưu danh sách chứng nhận.
* Mỗi ProductBatch có thể có nhiều Certification và mỗi Certification có thể áp dụng cho nhiều ProductBatch.
* Thiết kế bảng trung gian BatchCertification để biểu diễn quan hệ Many-to-Many.
* Chỉ rõ Primary Key (PK) và Foreign Key (FK).
* Xác định đầy đủ các quan hệ 1-N và N-N.

---

## AI phản hồi

AI xác định các thực thể gồm:

* User
* Farm
* CropType
* ProductBatch
* Certification
* BatchCertification
* Order
* LogisticsRequest

Quan hệ:

* User (Farmer) 1 - N Farm
* Farm 1 - N ProductBatch
* CropType 1 - N ProductBatch
* ProductBatch N - N Certification thông qua BatchCertification
* User (Buyer) 1 - N Order
* ProductBatch 1 - N Order
* Order 1 - 1 LogisticsRequest

---

## Prompt 2 (Cải thiện)

Hãy tạo mã Mermaid ERD hoàn chỉnh dựa trên các thực thể trên.

Yêu cầu:

* Thể hiện đầy đủ khóa chính (PK) và khóa ngoại (FK).
* Bảng BatchCertification phải là bảng trung gian của quan hệ Many-to-Many.
* Bổ sung đầy đủ các thuộc tính quan trọng của từng bảng.
* Thể hiện đúng các lực lượng (Cardinality): 1-1, 1-N, N-N.
* Sơ đồ phải rõ ràng, có thể xuất trực tiếp thành hình PNG để đưa vào báo cáo.

---

## AI phản hồi sau khi cải thiện

AI sinh ra mã Mermaid hoàn chỉnh với đầy đủ:

* User
* Farm
* CropType
* ProductBatch
* Certification
* BatchCertification
* Order
* LogisticsRequest

Đồng thời thể hiện chính xác:

* User (Farmer) sở hữu nhiều Farm.
* Mỗi Farm quản lý nhiều ProductBatch.
* Mỗi ProductBatch thuộc một CropType.
* ProductBatch và Certification có quan hệ N-N thông qua BatchCertification.
* Buyer tạo nhiều Order.
* Một ProductBatch có thể được đặt trong nhiều Order.
* Mỗi Order sinh một LogisticsRequest để kết nối với dịch vụ Logistics.

---

## Nhận xét

Prompt lần đầu giúp AI xác định đúng các thực thể và quan hệ của hệ thống.

Sau khi cải thiện Prompt, AI sinh ra sơ đồ ERD đầy đủ hơn với các khóa PK/FK, bảng trung gian BatchCertification và toàn bộ các quan hệ nghiệp vụ. Sơ đồ đáp ứng đúng yêu cầu chuẩn hóa dữ liệu, hỗ trợ tìm kiếm theo chứng nhận nông sản và dễ dàng mở rộng hệ thống trong tương lai.
