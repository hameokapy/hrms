-- ============================================================
-- DATABASE: HRMS (Human Resource Management System)
-- ============================================================

USE master;
GO

-- Drop database if exists
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'HRMS2')
BEGIN
    ALTER DATABASE HRMS2 SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE HRMS2;
END
GO

CREATE DATABASE HRMS2;
GO

USE HRMS2;
GO

--DELETE FROM leave_requests;
--DELETE FROM user_role;
--DELETE FROM employees;
--DELETE FROM departments;
--DELETE FROM positions;

-- Mục đích: Định nghĩa vai trò (ADMIN, HR, MANAGER, EMPLOYEE)
CREATE TABLE roles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    role_name NVARCHAR(50) NOT NULL UNIQUE,
    description NVARCHAR(500)
);
GO

-- Mục đích: Định nghĩa quyền hạn (employee.create, payroll.approve, etc.)
CREATE TABLE permissions (
    id INT IDENTITY(1,1) PRIMARY KEY,
    permission_key NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(500)
);
GO

-- Mục đích: Gán quyền cho vai trò
CREATE TABLE role_permission (
    id INT IDENTITY(1,1) PRIMARY KEY,
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
	scope NVARCHAR(20) NOT NULL DEFAULT 'ALL' CHECK (scope IN ('ALL', 'DEPT', 'OWN')),
    assigned_date DATETIME DEFAULT GETDATE(),
    assigned_by NVARCHAR(100), 
    CONSTRAINT FK_RolePermission_Role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT FK_RolePermission_Permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    CONSTRAINT UQ_RolePermission UNIQUE (role_id, permission_id)
);
GO

-- Mục đích: Quản lý phòng ban (manager_id sẽ thêm sau khi có employees)
CREATE TABLE departments (
    id INT IDENTITY(1,1) PRIMARY KEY,    
    code NVARCHAR(20) NOT NULL UNIQUE, 
    name NVARCHAR(100) NOT NULL UNIQUE, 
    manager_id INT NULL, 
    location NVARCHAR(255),
    status NVARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_date DATETIME DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    modified_date DATETIME,
    modified_by NVARCHAR(100)
);
GO

-- Mục đích: Định nghĩa chức vụ + mức lương khung
CREATE TABLE positions (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    base_salary_level DECIMAL(18,2) NOT NULL,
	status NVARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    description NVARCHAR(500),
    created_date DATETIME DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    modified_date DATETIME,
    modified_by NVARCHAR(100)
);
GO

-- Mục đích: Hồ sơ nhân viên
CREATE TABLE employees (
    id INT IDENTITY(1,1) PRIMARY KEY,
    employee_code NVARCHAR(20) NOT NULL UNIQUE,
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    phone NVARCHAR(20) NOT NULL UNIQUE,
    department_id INT NOT NULL,
    position_id INT NOT NULL,
    status NVARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'ON_LEAVE', 'INACTIVE', 'PENDING')),
    created_date DATETIME DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    modified_date DATETIME,
    modified_by NVARCHAR(100),
    CONSTRAINT FK_Employee_Department FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT FK_Employee_Position FOREIGN KEY (position_id) REFERENCES positions(id)
);
GO

-- Mục đích: Quản lý đăng nhập, phân quyền
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL, -- Hashed password
    employee_id INT, -- Để UNIQUE kiểu mới ở CONSTRAINT dưới
    is_active BIT DEFAULT 1,
    last_login DATETIME,
    created_date DATETIME DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    modified_date DATETIME,
    modified_by NVARCHAR(100),
    CONSTRAINT FK_User_Employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);
GO
-- muốn employeeId null nhiều tuple đc, nhưng hễ có giá trị r thì phải unique thì cần CONSTRAINT sau:
CREATE UNIQUE INDEX UQ_users_employee_id_filtered ON users(employee_id) WHERE employee_id IS NOT NULL;
GO

-- Mục đích: Gán vai trò cho user (1 user có thể có nhiều role)
CREATE TABLE user_role (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    assigned_date DATETIME DEFAULT GETDATE(),
    assigned_by NVARCHAR(100), 
    CONSTRAINT FK_UserRole_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT FK_UserRole_Role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT UQ_UserRole UNIQUE (user_id, role_id)
);
GO

-- Thêm FK manager_id vào departments (sau khi có bảng employees)
ALTER TABLE departments
ADD CONSTRAINT FK_Department_Manager FOREIGN KEY (manager_id) REFERENCES employees(id);
GO

-- Mục đích: Quản lý hợp đồng lao động (liên quan đến tính lương)
CREATE TABLE contracts (
    id INT IDENTITY(1,1) PRIMARY KEY,
    employee_id INT NOT NULL,
    contract_number NVARCHAR(50) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NULL, -- NULL nếu là hợp đồng vô thời hạn
    base_salary DECIMAL(18,2) NOT NULL,
    status NVARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'EXPIRED', 'TERMINATED')),
    type NVARCHAR(20) NOT NULL CHECK (type IN ('PROBATION', 'FIXED_TERM', 'INDEFINITE')),
    notes NVARCHAR(500),
    created_date DATETIME DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    modified_date DATETIME,
    modified_by NVARCHAR(100),
    CONSTRAINT FK_Contract_Employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);
GO

-- Mục đích: Quản lý số ngày phép còn lại của nhân viên
CREATE TABLE leave_balance (
    id INT IDENTITY(1,1) PRIMARY KEY,
    employee_id INT NOT NULL,
    year INT NOT NULL,
    annual_total_days INT NOT NULL, 
    annual_used_days INT DEFAULT 0,
    annual_remaining_days AS (annual_total_days - annual_used_days),
	sick_total_days INT NOT NULL,
	sick_used_days INT DEFAULT 0,
	sick_remaining_days AS (sick_total_days - sick_used_days),
    created_date DATETIME DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    modified_date DATETIME, 
    modified_by NVARCHAR(100),
    CONSTRAINT FK_LeaveBalance_Employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT UQ_LeaveBalance_Employee_Year UNIQUE (employee_id, year),
    CONSTRAINT CK_LeaveBalance_Annual_Days CHECK (annual_used_days >= 0 AND (annual_total_days - annual_used_days) >= 0),
    CONSTRAINT CK_LeaveBalance_Sick_Days CHECK (sick_used_days >= 0 AND (sick_total_days - sick_used_days) >= 0)
);
GO

-- Mục đích: Quản lý đơn xin nghỉ phép
CREATE TABLE leave_requests (
    id INT IDENTITY(1,1) PRIMARY KEY,
    employee_id INT NOT NULL,
    type NVARCHAR(20) NOT NULL CHECK (type IN ('ANNUAL', 'SICK', 'UNPAID')),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason NVARCHAR(500),
    status NVARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    approved_by INT NULL, -- FK đến employees (người duyệt)
    approved_date DATETIME NULL, 
    created_date DATETIME DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    modified_date DATETIME,
    modified_by NVARCHAR(100),
    CONSTRAINT FK_LeaveRequest_Employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT FK_LeaveRequest_Approver FOREIGN KEY (approved_by) REFERENCES employees(id),
    CONSTRAINT CK_LeaveRequest_Dates CHECK (end_date >= start_date)
);
GO

-- Mục đích: Quản lý chấm công hàng ngày
CREATE TABLE attendance (
    id INT IDENTITY(1,1) PRIMARY KEY,
    employee_id INT NOT NULL,
    date DATE NOT NULL,
    check_in TIME(0) NULL,
    check_out TIME(0) NULL,
    status NVARCHAR(20) DEFAULT 'INCOMPLETE' CHECK (status IN ('PRESENT', 'LATE_EARLY', 'ABSENT', 'ON_LEAVE', 'SICK_LEAVE', 'UNPAID_LEAVE', 'INCOMPLETE')),
    work_hours DECIMAL(5,2) DEFAULT 0, -- Số giờ làm việc thực tế
	is_lock BIT DEFAULT 0,
    notes NVARCHAR(500),
    created_date DATETIME DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    modified_date DATETIME,
    modified_by NVARCHAR(100),
    CONSTRAINT FK_Attendance_Employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT UQ_Attendance_Employee_Date UNIQUE (employee_id, date)
);
GO

-- Mục đích: Bảng lương tổng hợp
CREATE TABLE payroll (
    id INT IDENTITY(1,1) PRIMARY KEY,
    employee_id INT NOT NULL,
    month INT NOT NULL CHECK (month BETWEEN 1 AND 12),
    year INT NOT NULL CHECK (year >= 2000),
    -- Các thông số cơ bản để tính
    standard_days INT DEFAULT 26,      -- Số ngày công chuẩn trong tháng
    work_days DECIMAL(4,2) DEFAULT 0,  -- Số ngày làm việc thực tế (từ Attendance)
	sick_days INT DEFAULT 0, 
    unpaid_days INT DEFAULT 0,
    -- Các khoản thu nhập (Earnings)
    base_salary DECIMAL(18,2) NOT NULL, -- Lấy từ hợp đồng tại thời điểm tính
    overtime_pay DECIMAL(18,2) DEFAULT 0,
    allowances DECIMAL(18,2) DEFAULT 0, -- Phụ cấp (ăn trưa, xăng xe...)
    bonus DECIMAL(18,2) DEFAULT 0,      -- Thưởng hiệu suất, dự án...
    -- Các khoản khấu trừ (Deductions)
    absences_deduction DECIMAL(18,2) DEFAULT 0, -- Trừ lương nghỉ không phép/đi muộn
    insurance_deduction DECIMAL(18,2) DEFAULT 0, -- Bảo hiểm xã hội, y tế
    tax_deduction DECIMAL(18,2) DEFAULT 0,       -- Thuế TNCN (nếu có)
    -- Tổng cộng
    gross_salary DECIMAL(18,2) NOT NULL, -- Tổng thu nhập chưa trừ
    net_salary DECIMAL(18,2) NOT NULL,   -- Lương thực nhận (gross - các khoản trừ)
    -- Thông tin thanh toán & Phê duyệt
    status NVARCHAR(20) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'APPROVED', 'PAID')),
    payment_date DATETIME NULL,          -- Cập nhật khi status -> PAID
    approved_by INT NULL,                -- FK đến employees(id)
    approved_date DATETIME NULL,
    notes NVARCHAR(500),
    created_date DATETIME DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    modified_date DATETIME,
    modified_by NVARCHAR(100),
    CONSTRAINT FK_Payroll_Employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT FK_Payroll_Approver FOREIGN KEY (approved_by) REFERENCES employees(id),
    CONSTRAINT UQ_Payroll_Employee_Month_Year UNIQUE (employee_id, month, year)
);
GO
