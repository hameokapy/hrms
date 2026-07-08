
package com.hrms.core.constant;

public class PermissionConstants {
    
    private PermissionConstants() {}

    public static final class User {
        // Admin (SCOPE: ALL)
        public static final String VIEW_ALL = "user.view_all";
        // Admin (SCOPE: ALL)
        public static final String VIEW_DETAIL = "user.view_detail";
        // Admin (SCOPE: ALL)
        public static final String CREATE = "user.create";
        // Admin (SCOPE: ALL)
        public static final String BIND_EMPLOYEE = "user.bind_employee";
        // Admin (SCOPE: ALL): soft delete
        public static final String TOGGLE_STATUS = "user.toggle_status";
        // Admin (SCOPE: ALL)
        public static final String ASSIGN_ROLE = "user.assign_role";
        // Admin (SCOPE: ALL)
        public static final String RESET_PASSWORD = "user.reset_password";
        // HR+Admin+Employee+Manager (SCOPE: OWN)
        public static final String CHANGE_PASSWORD = "user.change_password";
    }
    
    public static final class Role {
        // Admin (SCOPE: ALL)
        public static final String VIEW = "role.view";
        // Admin (SCOPE: ALL)
        public static final String UPDATE = "role.update_description";
    }
    
    public static final class Permission {
        // Admin (SCOPE: ALL)
        public static final String VIEW = "permission.view";
        // Admin (SCOPE: ALL)
        public static final String UPDATE = "permission.update_description";
    }
    
    public static final class Department {
        // HR+Admin+Employee+Manager (SCOPE: ALL)
        public static final String VIEW = "department.view";
        // HR+Admin (SCOPE: ALL), Employee+Manager (SCOPE: DEPT)
        public static final String VIEW_DETAIL = "department.view_detail";
        // HR+Admin (SCOPE: ALL)
        public static final String CREATE = "department.create";
        // HR+Admin (SCOPE: ALL)
        public static final String EDIT = "department.edit";
        // HR+Admin (SCOPE: ALL)
        public static final String ASSIGN_MANAGER = "department.assign_manager";
        // Admin (SCOPE: ALL): soft delete
        public static final String DELETE = "department.delete";
        //Chức năng "Xem NV của Dept": link tới "employee.view" truyền WHERE department_id=?
    }

    public static final class Position {
        // HR+Admin (SCOPE: ALL)
        public static final String VIEW = "position.view";
        // HR+Admin (SCOPE: ALL)
        public static final String VIEW_DETAIL = "position.view_detail";
        // HR+Admin (SCOPE: ALL)
        public static final String CREATE = "position.create";
        // HR+Admin (SCOPE: ALL)
        public static final String EDIT = "position.edit";
        // Admin (SCOPE: ALL): soft delete
        public static final String DELETE = "position.delete";
    }

    public static final class Employee {
        // HR+Admin (SCOPE: ALL), Manager+Employee (SCOPE: DEPT)
        public static final String VIEW = "employee.view";
        // HR+Admin (SCOPE: ALL), Manager (SCOPE: DEPT), Employee (SCOPE: OWN)
        public static final String VIEW_DETAIL = "employee.view_detail";
        // HR+Admin (SCOPE: ALL)
        public static final String CREATE = "employee.create";
        // HR+Admin (SCOPE: ALL), Employee (SCOPE: OWN)
        public static final String EDIT = "employee.edit";
        // HR+Admin (SCOPE: ALL)
        public static final String ASSIGN_DEPT = "employee.assign_dept";
        // HR+Admin (SCOPE: ALL)
        public static final String ASSIGN_POSI = "employee.assign_posi";
        // Admin (SCOPE: ALL): soft delete
        public static final String DELETE = "employee.delete";
    }

    public static final class Leave {
        // Admin (SCOPE: ALL), Employee (SCOPE: OWN)
        public static final String CREATE_REQUEST = "leave.create_request";
        // Admin (SCOPE: ALL), Manager (SCOPE: DEPT), Employee (SCOPE: OWN)
        public static final String VIEW_REQUEST = "leave.view_request";
        // Admin (SCOPE: ALL), Employee (SCOPE: OWN): chỉ khi status còn pending
        public static final String UPDATE_REQUEST = "leave.update_request";
        // Admin (SCOPE: ALL), Employee (SCOPE: OWN): chỉ khi status còn pending
        public static final String CANCEL_REQUEST = "leave.cancel_request";
        // Admin (SCOPE: ALL), Manager (SCOPE: DEPT): cho phép sửa approved/rejected
        public static final String APPROVE_REQUEST = "leave.approve_request";
        // HR+Admin (SCOPE: ALL), Manager (SCOPE: DEPT), Employee (SCOPE: OWN)
        public static final String VIEW_BALANCE = "leave.view_balance";
    } 
   
    
    /* ======================== CHƯA LÀM MẤY CHỨC NĂNG DƯỚI ĐÂY ============================ */
    
    public static final class Contract { // Ko có chức năng edit vì coi hợp đồng ko đc chỉnh
        // HR+Admin (SCOPE: ALL), Employee (SCOPE: OWN)
        public static final String VIEW_OWN = "contract.view";
        // HR+Admin (SCOPE: ALL), Employee (SCOPE: OWN)
        public static final String VIEW_DETAIL = "contract.view_detail";
        // HR+Admin (SCOPE: ALL)
        public static final String CREATE = "contract.create";
        // Admin (SCOPE: ALL): soft delete
        public static final String DELETE = "contract.delete";
        //TODO: tự động chuyển từ employee pending sang active khi có hợp đồng và start date có hiệu lực
        //TODO: changeStatus() bên EmployeeService chưa làm đống lq đến contract!!!
    }

    public static final class Attendance {
        // Employee (SCOPE: OWN)
        public static final String CHECKIN = "attendance.checkin";
        // Employee (SCOPE: OWN)
        public static final String CHECKOUT = "attendance.checkout";
        // HR+Admin (SCOPE: ALL), Manager (SCOPE: DEPT), Employee (SCOPE: OWN)
        // Luôn luôn đòi param month và year, view sẽ hiển thị theo tháng
        // Phân tầng: Hiện full dept (ko data), vào dept hiện dsach employees (hiện data sơ bộ attendence tháng đó), vào employee (hiện lịch sử attendence từng ngày tháng đó)
        // ở tầng trong dept: tên NV, mã NV, tổng ngày công thực tế, số ngày đi muộn, số ngày nghỉ phép/ko phép
        // Index: Đảm bảo các cột (employee_id, date) và (department_id) được đánh Index trong DB để việc drill-down (ấn mở rộng) không bị lag.
        public static final String VIEW = "attendance.view";
        // HR+Admin (SCOPE:ALL)
        // ở tầng trong employee, sẽ hiện nút edit cho từng dòng lịch sử attendence tháng đó
        public static final String EDIT = "attendance.edit";
        // tính năng ẩn: cột is_lock: tự động đc set khi có chốt payroll của tháng, sau đó ko ai đc sửa gì attendance các ngày tháng đó
        
        // đánh compount index (empId, workingHours) để nó search thì thần tốc đc
        // work_hours bên này để ý hệ leave_request là xin nghỉ cả ngày (chứ ko phải nửa buổi), và nếu ngày nào ở diện nghỉ status nào ấy thì set work_hours = 0 mới hợp lý logic
    }
    
    
//Attendance: Đúng là dữ liệu quá khứ không đổi. Nhưng tương lai thì:
//INACTIVE: Chặn Check-in.
//ON_LEAVE: Thường hệ thống tự tạo record "Nghỉ" thay vì nhân viên tự check-in -> maybe cx chặn checkin, cho tới khi bỏ status onleave ở employee mới đc checkin như bthg
//PENDING: Chưa cho phép chấm công vì chưa vào làm chính thức? hmm mà thật ra vẫn có chứ nhỉ??
    
//    1. Luồng từ Leave Request (Dữ liệu gốc)
//      Khi một đơn nghỉ phép được APPROVED, nó sẽ "bắn" dữ liệu đi 2 hướng cùng lúc:
//      Hướng 1: Sang Leave Balance (Để trừ quỹ phép)
//            ANNUAL: Trừ vào used_days và remaining_days của phép năm.
//            SICK: Trừ vào sick_used_days (như mình vừa bàn là nên thêm field này).
//            UNPAID: Không làm gì bảng balance cả.
//      Hướng 2: Sang Attendance (Để ghi nhận trạng thái ngày đó)
//            Hệ thống sẽ tự động cập nhật (hoặc chèn mới) các bản ghi trong bảng attendance của nhân viên đó trong khoảng từ start_date đến end_date.
//            Status lúc này sẽ chuyển từ ABSENT hoặc INCOMPLETE sang ON_LEAVE, SICK_LEAVE hoặc UNPAID_LEAVE. Đây chính là câu trả lời cho ý "ảnh hưởng status attendance" của ông.
//    2. Luồng từ Attendance sang Payroll (Dữ liệu tính toán)
//            Bảng Payroll thường không lấy dữ liệu trực tiếp từ Leave Request hay Leave Balance để tính tiền, mà nó lấy từ Attendance.
//            Tại sao? Vì bảng Attendance là "bức tranh tổng quát" về một ngày của nhân viên.Cách Payroll "quét" Attendance:
//            Nó đếm các ngày có status PRESENT, LATE_EARLY, và ON_LEAVE để tính vào work_days (Những ngày này vẫn được trả lương).
//            Nó đếm các ngày SICK_LEAVE, UNPAID_LEAVE, ABSENT để tính vào absences_deduction (Những ngày này bị trừ lương khỏi base_salary).
//    3. Tóm tắt sơ đồ luồng dữ liệu
//            Leave Request (Approved) -> Cập nhật Leave Balance (Quản lý kho phép).
//            Leave Request (Approved) -> Cập nhật Attendance (Ghi nhận trạng thái ngày công).
//            Attendance (Cuối tháng) -> Đẩy số liệu sang Payroll (Tính ra tiền thực nhận).
//            Leave Request và Leave Balance ko ảnh hưởng Payroll vì chuyển cho Attendance rồi
//            Hiện chỉ có Attendance và salary ở Contract -> Ảnh hưởng tính toán Payroll
    
//Khi viết hàm calculateMonthlyPayroll, logic sẽ chạy như thế này:
//Quét Attendance tháng đó:
//    count_ON_LEAVE, count_PRESENT...
//    count_SICK = Đếm số ngày SICK_LEAVE.
//    count_UNPAID = Đếm số ngày (UNPAID_LEAVE + ABSENT).
//Ghi vào Payroll:
//    payroll.sick_days = count_SICK
//    payroll.unpaid_days = count_UNPAID
//    payroll.work_days = count_PRESENT + count_ON_LEAVE
//    payroll.absences_deduction = (base_salary / standard_days) * (count_SICK + count_UNPAID)

    public static final class Payroll {
        // HR+Admin (SCOPE: ALL), Manager (SCOPE: DEPT), Employee (SCOPE: OWN): hiển thị theo tháng
        // level all (thêm cột tổng chi trả cả công ty), level dept (thêm cột tổng chi trả của cả phòng)
        public static final String VIEW = "payroll.view";
        // HR+Admin (SCOPE: ALL): chỉ là ấn nút thôi (hệ thống tính toán ngầm)
        public static final String CALCULATE = "payroll.calculate";
        // Admin (SCOPE: ALL): cập nhật mấy field approve
        public static final String APPROVE = "payroll.approve";
        // Admin (SCOPE: ALL): cập nhật mấy field pay
        public static final String PAY = "payroll.pay";
        // NOTE: employee mà bị inactive, miễn xuất hiện attendance ngày nào trong tháng đó có work day > 0 vẫn tính tiền
        // NOTE: cột notes để ghi kiểu nhân viên bỏ việc giữa tháng
        // NOTE: leave_request và type (ANNUAL hay UNPAID) mới dùng để tính tiền bên payroll (chứ not leave_balance vì cái này theo năm) còn 
        // nhìn vào leave_request thuộc tháng nào để tính payroll tháng đó
    }
}

/*      BẢNG PHÂN SCOPE:
    I. MODULE EMPLOYEE & USER & ROLE & PERMISSION
    1, employee.view, all
    2, employee.view, all
    3, employee.view, dept
    4, employee.view, dept
    1, employee.view_detail, all
    2, employee.view_detail, all
    3, employee.view_detail, dept
    4, employee.view_detail, own
    1, employee.create, all
    2, employee.create, all
    1, employee.edit, all
    2, employee.edit, all
    4, employee.edit, own
    1, employee.assign_dept, all
    2, employee.assign_dept, all
    1, employee.assign_posi, all
    2, employee.assign_posi, all
    1, employee.delete, all 

    1, user.view_all, all
    1, user.view_detail, all
    1, user.create, all
    1, user.bind_employee, all
    1, user.toggle_status, all
    1, user.assign_role, all
    1, user.reset_password, all
    1, user.change_password, own
    2, user.change_password, own
    3, user.change_password, own
    4, user.change_password, own

    1, role.view, all
    1, role.update_description, all
    1, permission.view, all
    1, permission.update_description, all

    II. MODULE DEPARTMENT & POSITION
    1, department.view, all
    2, department.view, all
    3, department.view, all
    4, department.view, all
    1, department.view_detail, all
    2, department.view_detail, all
    3, department.view_detail, dept
    4, department.view_detail, dept
    1, department.create, all
    2, department.create, all
    1, department.edit, all
    2, department.edit, all
    1, department.assign_manager, all
    2, department.assign_manager, all
    1, department.delete, all

    1, position.view, all
    2, position.view, all
    1, position.view_detail, all
    2, position.view_detail, all
    1, position.create, all
    2, position.create, all
    1, position.edit, all
    2, position.edit, all
    1, position.delete, all

    III. MODULE ATTENDANCE & LEAVE
    4, attendance.checkin, own
    4, attendance.checkout, own
    1, attendance.view, all
    2, attendance.view, all
    3, attendance.view, dept
    4, attendance.view, own
    1, attendance.edit, all
    2, attendance.edit, all

    1, leave.create_request, all
    4, leave.create_request, own
    1, leave.view_request, all
    3, leave.view_request, dept
    4, leave.view_request, own
    1, leave.update_request, all
    4, leave.update_request, own
    1, leave.cancel_request, all
    4, leave.cancel_request, own
    1, leave.approve_request, all
    3, leave.approve_request, dept
    1, leave.view_balance, all
    2, leave.view_balance, all
    3, leave.view_balance, dept
    4, leave.view_balance, own

    IV. MODULE PAYROLL & CONTRACT
    1, payroll.view, all
    2, payroll.view, all
    3, payroll.view, dept
    4, payroll.view, own
    1, payroll.calculate, all
    2, payroll.calculate, all
    1, payroll.approve, all
    1, payroll.pay, all

    1, contract.view, all
    2, contract.view, all
    4, contract.view, own
    1, contract.view_detail, all
    2, contract.view_detail, all
    4, contract.view_detail, own
    1, contract.create, all
    2, contract.create, all
    1, contract.delete, all
*/