-- ============================================
-- 1. ROLES (4 roles)
-- ============================================
SET IDENTITY_INSERT roles ON;
INSERT INTO roles (id, role_name, description) VALUES
(1, 'ADMIN', 'System Administrator - Full access'),
(2, 'HR', 'Human Resources - Manage employees, contracts, leaves, payroll'),
(3, 'MANAGER', 'Department Manager - Manage department team and approve leaves'),
(4, 'EMPLOYEE', 'Employee - Basic access to own data');
SET IDENTITY_INSERT roles OFF;
GO

-- ============================================
-- 2. PERMISSIONS (theo PermissionConstants)
-- ============================================
SET IDENTITY_INSERT permissions ON;

-- Employee permissions (1-7)
INSERT INTO permissions (id, permission_key, description) VALUES
(1, 'employee.view', 'View employee list'),
(2, 'employee.view_detail', 'View employee details'),
(3, 'employee.create', 'Create new employee'),
(4, 'employee.edit', 'Edit employee information'),
(5, 'employee.assign_dept', 'Assign employee to department'),
(6, 'employee.assign_posi', 'Assign employee position'),
(7, 'employee.delete', 'Delete employee (soft delete)');

-- Department permissions (8-13)
INSERT INTO permissions (id, permission_key, description) VALUES
(8, 'department.view', 'View department list'),
(9, 'department.view_detail', 'View department details'),
(10, 'department.create', 'Create new department'),
(11, 'department.edit', 'Edit department'),
(12, 'department.assign_manager', 'Assign department manager'),
(13, 'department.delete', 'Delete department (soft delete)');

-- Position permissions (14-18)
INSERT INTO permissions (id, permission_key, description) VALUES
(14, 'position.view', 'View position list'),
(15, 'position.view_detail', 'View position details'),
(16, 'position.create', 'Create new position'),
(17, 'position.edit', 'Edit position'),
(18, 'position.delete', 'Delete position (soft delete)');

-- Contract permissions (19-22)
INSERT INTO permissions (id, permission_key, description) VALUES
(19, 'contract.view', 'View contract list'),
(20, 'contract.view_detail', 'View contract details'),
(21, 'contract.create', 'Create new contract'),
(22, 'contract.delete', 'Delete contract (soft delete)');

-- Leave permissions (23-28)
INSERT INTO permissions (id, permission_key, description) VALUES
(23, 'leave.create_request', 'Create leave request'),
(24, 'leave.view_request', 'View leave requests'),
(25, 'leave.update_request', 'Update leave request'),
(26, 'leave.cancel_request', 'Cancel leave request'),
(27, 'leave.approve_request', 'Approve/Reject leave request'),
(28, 'leave.view_balance', 'View leave balance');

-- Attendance permissions (29-32)
INSERT INTO permissions (id, permission_key, description) VALUES
(29, 'attendance.checkin', 'Check-in attendance'),
(30, 'attendance.checkout', 'Check-out attendance'),
(31, 'attendance.view', 'View attendance records'),
(32, 'attendance.edit', 'Edit attendance records');

-- Payroll permissions (33-36)
INSERT INTO permissions (id, permission_key, description) VALUES
(33, 'payroll.view', 'View payroll'),
(34, 'payroll.calculate', 'Calculate payroll'),
(35, 'payroll.approve', 'Approve payroll'),
(36, 'payroll.pay', 'Mark payroll as paid');

-- User management permissions (37-48)
INSERT INTO permissions (id, permission_key, description) VALUES
(37, 'user.view_all', 'View all users'),
(38, 'user.view_detail', 'View user details'),
(39, 'user.create', 'Create new user'),
(40, 'user.bind_employee', 'Bind user to employee'),
(41, 'user.toggle_status', 'Enable/Disable user'),
(42, 'user.assign_role', 'Assign role to user'),
(43, 'user.reset_password', 'Reset user password'),
(44, 'role.view', 'View roles'),
(45, 'role.update_description', 'Update role description'),
(46, 'permission.view', 'View permissions'),
(47, 'permission.update_description', 'Update permission description'),
(48, 'user.change_password', 'Change own password');

SET IDENTITY_INSERT permissions OFF;
GO

-- ============================================
-- 3. ROLE-PERMISSION MAPPING
-- ============================================

-- MODULE I: EMPLOYEE & USER & ROLE & PERMISSION
INSERT INTO role_permission (role_id, permission_id, scope, assigned_by)
SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.view'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.view'
UNION ALL SELECT 3, id, 'DEPT', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.view'
UNION ALL SELECT 4, id, 'DEPT', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.view'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.view_detail'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.view_detail'
UNION ALL SELECT 3, id, 'DEPT', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.view_detail'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.view_detail'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.create'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.create'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.edit'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.edit'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.edit'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.assign_dept'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.assign_dept'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.assign_posi'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.assign_posi'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'employee.delete'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'user.view_all'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'user.view_detail'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'user.create'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'user.bind_employee'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'user.toggle_status'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'user.assign_role'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'user.reset_password'
UNION ALL SELECT 1, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'user.change_password'
UNION ALL SELECT 2, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'user.change_password'
UNION ALL SELECT 3, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'user.change_password'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'user.change_password'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'role.view'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'role.update_description'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'permission.view'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'permission.update_description';
GO

-- MODULE II: DEPARTMENT & POSITION
INSERT INTO role_permission (role_id, permission_id, scope, assigned_by)
SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.view'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.view'
UNION ALL SELECT 3, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.view'
UNION ALL SELECT 4, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.view'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.view_detail'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.view_detail'
UNION ALL SELECT 3, id, 'DEPT', 'SYSTEM' FROM permissions WHERE permission_key = 'department.view_detail'
UNION ALL SELECT 4, id, 'DEPT', 'SYSTEM' FROM permissions WHERE permission_key = 'department.view_detail'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.create'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.create'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.edit'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.edit'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.assign_manager'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.assign_manager'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'department.delete'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'position.view'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'position.view'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'position.view_detail'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'position.view_detail'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'position.create'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'position.create'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'position.edit'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'position.edit'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'position.delete';
GO

-- MODULE III: ATTENDANCE & LEAVE
INSERT INTO role_permission (role_id, permission_id, scope, assigned_by)
SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'attendance.checkin'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'attendance.checkout'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'attendance.view'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'attendance.view'
UNION ALL SELECT 3, id, 'DEPT', 'SYSTEM' FROM permissions WHERE permission_key = 'attendance.view'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'attendance.view'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'attendance.edit'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'attendance.edit'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.create_request'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.create_request'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.view_request'
UNION ALL SELECT 3, id, 'DEPT', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.view_request'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.view_request'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.update_request'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.update_request'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.cancel_request'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.cancel_request'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.approve_request'
UNION ALL SELECT 3, id, 'DEPT', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.approve_request'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.view_balance'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.view_balance'
UNION ALL SELECT 3, id, 'DEPT', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.view_balance'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'leave.view_balance';
GO

-- MODULE IV: PAYROLL & CONTRACT
INSERT INTO role_permission (role_id, permission_id, scope, assigned_by)
SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'payroll.view'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'payroll.view'
UNION ALL SELECT 3, id, 'DEPT', 'SYSTEM' FROM permissions WHERE permission_key = 'payroll.view'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'payroll.view'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'payroll.calculate'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'payroll.calculate'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'payroll.approve'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'payroll.pay'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'contract.view'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'contract.view'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'contract.view'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'contract.view_detail'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'contract.view_detail'
UNION ALL SELECT 4, id, 'OWN', 'SYSTEM' FROM permissions WHERE permission_key = 'contract.view_detail'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'contract.create'
UNION ALL SELECT 2, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'contract.create'
UNION ALL SELECT 1, id, 'ALL', 'SYSTEM' FROM permissions WHERE permission_key = 'contract.delete';
GO

-- ============================================
-- 4. DEPARTMENTS (7 departments)
-- ============================================
SET IDENTITY_INSERT departments ON;
INSERT INTO departments (id, code, name, location, status, created_by, created_date) VALUES
(1, 'IT', N'Phòng Công Nghệ Thông Tin', N'Tầng 5, Tòa A', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(2, 'HR', N'Phòng Nhân Sự', N'Tầng 2, Tòa A', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(3, 'SALES', N'Phòng Kinh Doanh', N'Tầng 1, Tòa B', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(4, 'FINANCE', N'Phòng Kế Toán', N'Tầng 3, Tòa A', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(5, 'MARKETING', N'Phòng Marketing', N'Tầng 2, Tòa B', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(6, 'OPERATION', N'Phòng Vận Hành', N'Tầng 1, Tòa A', 'ACTIVE', 'SYSTEM', '2024-06-01'),
(7, 'OLD_DEPT', N'Phòng Ban Cũ', N'Tầng 4, Tòa C', 'INACTIVE', 'SYSTEM', '2023-01-01'),
(8, 'SUPPORT', N'Phòng Chăm Sóc Khách Hàng', N'Tầng 4, Tòa B', 'ACTIVE', 'SYSTEM', '2024-08-15'),
(9, 'TRAINING', N'Phòng Đào Tạo', N'Tầng 6, Tòa A', 'ACTIVE', 'SYSTEM', '2024-09-10'),
(10, 'RD', N'Phòng Nghiên Cứu và Phát Triển', N'Tầng 5, Tòa B', 'ACTIVE', 'SYSTEM', '2025-01-20'),
(11, 'LOGISTICS', N'Phòng Kho Vận', N'Khu Công Nghiệp A', 'ACTIVE', 'SYSTEM', '2025-02-12');
SET IDENTITY_INSERT departments OFF;
GO

-- ============================================
-- 5. POSITIONS (10 positions)
-- ============================================
SET IDENTITY_INSERT positions ON;
INSERT INTO positions (id, name, base_salary_level, description, status, created_by, created_date) VALUES
(1, N'Giám Đốc', 50000000, N'Director', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(2, N'Trưởng Phòng', 30000000, N'Department Manager', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(3, N'Phó Phòng', 25000000, N'Deputy Manager', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(4, N'Nhân Viên Senior', 20000000, N'Senior Staff', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(5, N'Nhân Viên', 15000000, N'Staff', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(6, N'Nhân Viên Junior', 12000000, N'Junior Staff', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(7, N'Thực Tập Sinh', 5000000, N'Intern', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(8, N'Kế Toán Trưởng', 35000000, N'Chief Accountant', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(9, N'Trưởng Dự Án', 28000000, N'Project Manager', 'ACTIVE', 'SYSTEM', '2024-01-01'),
(10, N'Chức Vụ Cũ', 10000000, N'Old Position', 'INACTIVE', 'SYSTEM', '2023-01-01'),
(11, N'Chuyên Viên Tư Vấn', 18000000, N'Consultant', 'ACTIVE', 'SYSTEM', '2025-03-27');
SET IDENTITY_INSERT positions OFF;
GO

-- ============================================
-- 6. EMPLOYEES (25 employees)
-- ============================================
SET IDENTITY_INSERT employees ON;
INSERT INTO employees (id, employee_code, full_name, email, phone, department_id, position_id, status, created_by, created_date) VALUES
-- IT Department (5 người) 
(1, 'EMP001', N'Nguyễn Văn Admin', 'admin@company.com', '0901234567', 1, 1, 'ACTIVE', 'SYSTEM', '2024-01-01'),
(2, 'EMP002', N'Trần Thị IT Manager', 'it.manager@company.com', '0901234568', 1, 2, 'ACTIVE', 'SYSTEM', '2024-01-01'),
(3, 'EMP003', N'Lê Văn Developer 1', 'dev1@company.com', '0901234569', 1, 4, 'ACTIVE', 'SYSTEM', '2024-02-01'),
(4, 'EMP004', N'Phạm Thị Developer 2', 'dev2@company.com', '0901234570', 1, 5, 'ACTIVE', 'SYSTEM', '2024-03-01'),
(5, 'EMP005', N'Hoàng Văn Intern IT', 'intern.it@company.com', '0901234571', 1, 7, 'ACTIVE', 'SYSTEM', '2025-01-15'),

-- HR Department (4 người) 
(6, 'EMP006', N'Đỗ Thị HR Manager', 'hr.manager@company.com', '0901234572', 2, 2, 'ACTIVE', 'SYSTEM', '2024-01-01'),
(7, 'EMP007', N'Vũ Văn HR Staff 1', 'hr.staff1@company.com', '0901234573', 2, 5, 'ACTIVE', 'SYSTEM', '2024-02-01'),
(8, 'EMP008', N'Bùi Thị HR Staff 2', 'hr.staff2@company.com', '0901234574', 2, 5, 'ACTIVE', 'SYSTEM', '2024-04-01'),
(9, 'EMP009', N'Đinh Văn Recruiter', 'recruiter@company.com', '0901234575', 2, 6, 'ACTIVE', 'SYSTEM', '2024-08-01'),

-- Sales Department (11 người) 
(10, 'EMP010', N'Mai Thị Sales Manager', 'sales.manager@company.com', '0901234576', 3, 2, 'ACTIVE', 'SYSTEM', '2024-01-01'),
(11, 'EMP011', N'Lý Văn Sales Senior', 'sales.senior@company.com', '0901234577', 3, 4, 'ACTIVE', 'SYSTEM', '2024-02-01'),
(12, 'EMP012', N'Phan Thị Sales 1', 'sales1@company.com', '0901234578', 3, 5, 'ACTIVE', 'SYSTEM', '2024-03-01'),
(13, 'EMP013', N'Chu Văn Sales 2', 'sales2@company.com', '0901234579', 3, 5, 'ON_LEAVE', 'SYSTEM', '2024-05-01'),
(14, 'EMP014', N'Trịnh Thị Sales 3', 'sales3@company.com', '0901234580', 3, 6, 'ACTIVE', 'SYSTEM', '2024-07-01'),
(26, 'EMP026', N'Ngô Văn Phó Phòng Sales', 'sales.deputy@company.com', '0901234592', 3, 3, 'ACTIVE', 'SYSTEM', '2024-08-15'),
(27, 'EMP027', N'Nguyễn Thị Tư Vấn 1', 'sales.consult1@company.com', '0901234593', 3, 11, 'ACTIVE', 'SYSTEM', '2025-01-10'),
(28, 'EMP028', N'Trần Văn Tư Vấn 2', 'sales.consult2@company.com', '0901234594', 3, 11, 'ACTIVE', 'SYSTEM', '2025-01-12'),
(29, 'EMP029', N'Lê Thị Sales 4', 'sales4@company.com', '0901234595', 3, 5, 'ACTIVE', 'SYSTEM', '2025-02-01'),
(30, 'EMP030', N'Phạm Văn Sales 5', 'sales5@company.com', '0901234596', 3, 5, 'ACTIVE', 'SYSTEM', '2025-02-10'),
(31, 'EMP031', N'Vũ Thị Intern Sales', 'sales.intern@company.com', '0901234597', 3, 7, 'ACTIVE', 'SYSTEM', '2025-03-01'),

-- Finance Department (4 người) 
(15, 'EMP015', N'Ngô Văn Finance Manager', 'finance.manager@company.com', '0901234600', 4, 8, 'ACTIVE', 'SYSTEM', '2024-01-01'),
(16, 'EMP016', N'Dương Thị Accountant 1', 'accountant1@company.com', '0901234601', 4, 5, 'ACTIVE', 'SYSTEM', '2024-02-01'),
(17, 'EMP017', N'Võ Văn Accountant 2', 'accountant2@company.com', '0901234602', 4, 5, 'ACTIVE', 'SYSTEM', '2024-04-01'),
(18, 'EMP018', N'Tô Thị Junior Acc', 'junior.acc@company.com', '0901234603', 4, 6, 'ACTIVE', 'SYSTEM', '2024-09-01'),

-- Marketing Department (4 người) 
(19, 'EMP019', N'Lâm Văn Marketing Manager', 'marketing.manager@company.com', '0901234604', 5, 2, 'ACTIVE', 'SYSTEM', '2024-01-01'),
(20, 'EMP020', N'Hồ Thị Marketing Staff 1', 'marketing1@company.com', '0901234605', 5, 5, 'ACTIVE', 'SYSTEM', '2024-03-01'),
(21, 'EMP021', N'Tạ Văn Marketing Staff 2', 'marketing2@company.com', '0901234606', 5, 5, 'ACTIVE', 'SYSTEM', '2024-05-01'),
(22, 'EMP022', N'Đặng Thị Content Creator', 'content@company.com', '0901234607', 5, 6, 'ACTIVE', 'SYSTEM', '2024-08-01'),

-- Operation Department (2 người)
(23, 'EMP023', N'Hà Văn Operation Manager', 'operation.manager@company.com', '0901234608', 6, 2, 'ACTIVE', 'SYSTEM', '2024-06-01'),
(24, 'EMP024', N'Kiều Thị Operation Staff', 'operation.staff@company.com', '0901234609', 6, 5, 'ACTIVE', 'SYSTEM', '2024-07-01'),

-- Resigned Employee (1 người - INACTIVE)
(25, 'EMP025', N'Lưu Văn Resigned', 'resigned.old@company.com', '0901234610', 3, 5, 'INACTIVE', 'SYSTEM', '2024-02-01');

SET IDENTITY_INSERT employees OFF;
GO

-- Update department managers
UPDATE departments SET manager_id = 2, modified_by = 'SYSTEM', modified_date = GETDATE() WHERE id = 1; -- IT
UPDATE departments SET manager_id = 6, modified_by = 'SYSTEM', modified_date = GETDATE() WHERE id = 2; -- HR
UPDATE departments SET manager_id = 10, modified_by = 'SYSTEM', modified_date = GETDATE() WHERE id = 3; -- Sales
UPDATE departments SET manager_id = 15, modified_by = 'SYSTEM', modified_date = GETDATE() WHERE id = 4; -- Finance
UPDATE departments SET manager_id = 19, modified_by = 'SYSTEM', modified_date = GETDATE() WHERE id = 5; -- Marketing
UPDATE departments SET manager_id = 23, modified_by = 'SYSTEM', modified_date = GETDATE() WHERE id = 6; -- Operation
GO

-- ============================================
-- 7. USERS (20 users) - Password: 123456 (as hashed below using BCrypt library)
-- ============================================
SET IDENTITY_INSERT users ON;
INSERT INTO users (id, username, password, employee_id, is_active, created_by, created_date) VALUES
-- System admin (no employee binding - edge case)
(1, 'sysadmin', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', NULL, 1, 'SYSTEM', '2024-01-01'),

-- Admin with employee
(2, 'admin', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 1, 1, 'SYSTEM', '2024-01-01'),

-- Managers
(3, 'it.manager', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 2, 1, 'SYSTEM', '2024-01-01'),
(4, 'hr.manager', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 6, 1, 'SYSTEM', '2024-01-01'),
(5, 'sales.manager', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 10, 1, 'SYSTEM', '2024-01-01'),
(6, 'finance.manager', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 15, 1, 'SYSTEM', '2024-01-01'),
(7, 'marketing.manager', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 19, 1, 'SYSTEM', '2024-01-01'),
(8, 'operation.manager', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 23, 1, 'SYSTEM', '2024-06-01'),

-- Employees
(9, 'dev1', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 3, 1, 'SYSTEM', '2024-02-01'),
(10, 'dev2', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 4, 1, 'SYSTEM', '2024-03-01'),
(11, 'hr.staff1', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 7, 1, 'SYSTEM', '2024-02-01'),
(12, 'hr.staff2', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 8, 1, 'SYSTEM', '2024-04-01'),
(13, 'sales.senior', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 11, 1, 'SYSTEM', '2024-02-01'),
(14, 'sales1', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 12, 1, 'SYSTEM', '2024-03-01'),
(15, 'accountant1', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 16, 1, 'SYSTEM', '2024-02-01'),
(16, 'accountant2', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 17, 1, 'SYSTEM', '2024-04-01'),
(17, 'marketing1', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 20, 1, 'SYSTEM', '2024-03-01'),
(18, 'marketing2', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', 21, 1, 'SYSTEM', '2024-05-01'),

-- Inactive user (resigned employee)
(19, 'resigned.user', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', NULL, 0, 'SYSTEM', '2024-02-01'),

-- User without employee binding (edge case)
(20, 'temp.user', '$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK', NULL, 1, 'SYSTEM', '2025-01-01');

SET IDENTITY_INSERT users OFF;
GO

--update users SET password='$2a$10$lyqB/LpMJNc5kVnWy92XNeMdWK33TQ/9g4q2nprKUZMffGwUpDtfK' --aka 123456

-- ============================================
-- 8. USER-ROLE MAPPING
-- ============================================
INSERT INTO user_role (user_id, role_id, assigned_by, assigned_date) VALUES
-- System admin
(1, 1, 'SYSTEM', '2024-01-01'),

-- Admin
(2, 1, 'SYSTEM', '2024-01-01'),

-- Managers (role = MANAGER)
(3, 3, 'SYSTEM', '2024-01-01'),
(5, 3, 'SYSTEM', '2024-01-01'),
(6, 3, 'SYSTEM', '2024-01-01'),
(7, 3, 'SYSTEM', '2024-01-01'),
(8, 3, 'SYSTEM', '2024-06-01'),

-- HR Manager (HR + MANAGER - edge case: multiple roles)
(4, 2, 'SYSTEM', '2024-01-01'),
(4, 3, 'SYSTEM', '2024-01-01'),

-- HR Staff (HR role)
(11, 2, 'SYSTEM', '2024-02-01'),
(12, 2, 'SYSTEM', '2024-04-01'),

-- Employees (EMPLOYEE role)
(9, 4, 'SYSTEM', '2024-02-01'),
(10, 4, 'SYSTEM', '2024-03-01'),
(13, 4, 'SYSTEM', '2024-02-01'),
(14, 4, 'SYSTEM', '2024-03-01'),
(15, 4, 'SYSTEM', '2024-02-01'),
(16, 4, 'SYSTEM', '2024-04-01'),
(17, 4, 'SYSTEM', '2024-03-01'),
(18, 4, 'SYSTEM', '2024-05-01'),

-- Gán thêm role employee cho user id số 2, user id số 5, user id số 11
(2, 4, 'SYSTEM', '2026-03-27'),
(5, 4, 'SYSTEM', '2026-03-27'),
(11, 4, 'SYSTEM', '2026-03-27'),

-- Temp user (EMPLOYEE role - edge case)
(20, 4, 'SYSTEM', '2025-01-01');
GO

-- ============================================
-- 9. CONTRACTS (28 contracts - including expired/terminated)
-- ============================================
SET IDENTITY_INSERT contracts ON;
INSERT INTO contracts (id, employee_id, contract_number, start_date, end_date, base_salary, status, type, notes, created_by, created_date) VALUES
-- Active contracts
(1, 1, 'CT2024001', '2024-01-01', NULL, 50000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-01-01'),
(2, 2, 'CT2024002', '2024-01-01', NULL, 30000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-01-01'),
(3, 3, 'CT2024003', '2024-02-01', NULL, 20000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-02-01'),
(4, 4, 'CT2024004', '2024-03-01', '2025-03-01', 15000000, 'ACTIVE', 'FIXED_TERM', NULL, 'SYSTEM', '2024-03-01'),
(5, 5, 'CT2025005', '2025-01-15', '2025-04-15', 5000000, 'ACTIVE', 'PROBATION', N'Thực tập sinh', 'SYSTEM', '2025-01-15'),
(6, 6, 'CT2024006', '2024-01-01', NULL, 30000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-01-01'),
(7, 7, 'CT2024007', '2024-02-01', NULL, 15000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-02-01'),
(8, 8, 'CT2024008', '2024-04-01', NULL, 15000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-04-01'),
(9, 9, 'CT2024009', '2024-08-01', NULL, 12000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-08-01'),
(10, 10, 'CT2024010', '2024-01-01', NULL, 30000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-01-01'),
(11, 11, 'CT2024011', '2024-02-01', NULL, 20000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-02-01'),
(12, 12, 'CT2024012', '2024-03-01', NULL, 15000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-03-01'),
(13, 13, 'CT2024013', '2024-05-01', NULL, 15000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-05-01'),
(14, 14, 'CT2024014', '2024-07-01', '2025-07-01', 12000000, 'ACTIVE', 'FIXED_TERM', NULL, 'SYSTEM', '2024-07-01'),
(15, 15, 'CT2024015', '2024-01-01', NULL, 35000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-01-01'),
(16, 16, 'CT2024016', '2024-02-01', NULL, 15000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-02-01'),
(17, 17, 'CT2024017', '2024-04-01', NULL, 15000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-04-01'),
(18, 18, 'CT2024018', '2024-09-01', '2025-03-01', 12000000, 'ACTIVE', 'PROBATION', NULL, 'SYSTEM', '2024-09-01'),
(19, 19, 'CT2024019', '2024-01-01', NULL, 30000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-01-01'),
(20, 20, 'CT2024020', '2024-03-01', NULL, 15000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-03-01'),
(21, 21, 'CT2024021', '2024-05-01', NULL, 15000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-05-01'),
(22, 22, 'CT2024022', '2024-08-01', '2025-02-01', 12000000, 'ACTIVE', 'PROBATION', NULL, 'SYSTEM', '2024-08-01'),
(23, 23, 'CT2024023', '2024-06-01', NULL, 30000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-06-01'),
(24, 24, 'CT2024024', '2024-07-01', NULL, 15000000, 'ACTIVE', 'INDEFINITE', NULL, 'SYSTEM', '2024-07-01'),

-- Terminated contract (resigned employee)
(25, 25, 'CT2024025', '2024-02-01', '2024-11-30', 15000000, 'TERMINATED', 'FIXED_TERM', N'Nghỉ việc', 'SYSTEM', '2024-02-01'),

-- Expired contracts (edge case: employee had old contracts)
(26, 3, 'CT2023026', '2023-01-01', '2024-01-31', 18000000, 'EXPIRED', 'FIXED_TERM', N'Hợp đồng cũ', 'SYSTEM', '2023-01-01'),
(27, 11, 'CT2023027', '2023-01-01', '2024-01-31', 18000000, 'EXPIRED', 'FIXED_TERM', N'Hợp đồng cũ', 'SYSTEM', '2023-01-01'),
(28, 16, 'CT2023028', '2023-06-01', '2024-01-31', 13000000, 'EXPIRED', 'PROBATION', N'Thử việc', 'SYSTEM', '2023-06-01');

SET IDENTITY_INSERT contracts OFF;
GO

-- ============================================
-- 10.1 LEAVE BALANCE (2025 - cho tất cả active employees)
-- ============================================
SET IDENTITY_INSERT leave_balance ON;
INSERT INTO leave_balance (id, employee_id, year, annual_total_days, annual_used_days, sick_total_days, sick_used_days, created_by, created_date) VALUES
(1, 1, 2025, 14, 2, 7, 0, 'SYSTEM', '2025-01-01'),
(2, 2, 2025, 14, 1, 7, 0, 'SYSTEM', '2025-01-01'),
(3, 3, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(4, 4, 2025, 12, 3, 7, 1, 'SYSTEM', '2025-01-01'),
(5, 5, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-15'),
(6, 6, 2025, 14, 1, 7, 0, 'SYSTEM', '2025-01-01'),
(7, 7, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(8, 8, 2025, 12, 2, 7, 0, 'SYSTEM', '2025-01-01'),
(9, 9, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(10, 10, 2025, 14, 2, 7, 0, 'SYSTEM', '2025-01-01'),
(11, 11, 2025, 12, 1, 7, 0, 'SYSTEM', '2025-01-01'),
(12, 12, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(13, 13, 2025, 12, 5, 7, 2, 'SYSTEM', '2025-01-01'),
(14, 14, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(15, 15, 2025, 14, 1, 7, 0, 'SYSTEM', '2025-01-01'),
(16, 16, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(17, 17, 2025, 12, 1, 7, 0, 'SYSTEM', '2025-01-01'),
(18, 18, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(19, 19, 2025, 14, 2, 7, 0, 'SYSTEM', '2025-01-01'),
(20, 20, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(21, 21, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(22, 22, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(23, 23, 2025, 14, 0, 7, 0, 'SYSTEM', '2025-01-01'),
(24, 24, 2025, 12, 0, 7, 0, 'SYSTEM', '2025-01-01');
SET IDENTITY_INSERT leave_balance OFF;
GO

-- ============================================
-- 10.2 LEAVE BALANCE (2026 - cho tất cả employees có leave balance năm 2025)
-- ============================================

INSERT INTO leave_balance (employee_id, year, annual_total_days, annual_used_days, 
    sick_total_days, sick_used_days, created_by, created_date)
	SELECT employee_id, 2026, annual_total_days, 0, sick_total_days, 0, 'SYSTEM', GETDATE()
		FROM leave_balance WHERE year = 2025;
GO

-- ============================================
-- 11. LEAVE REQUESTS (15 requests với các status khác nhau)
-- ============================================
SET IDENTITY_INSERT leave_requests ON;
INSERT INTO leave_requests (id, employee_id, type, start_date, end_date, reason, status, approved_by, approved_date, created_by, created_date) VALUES
-- Approved annual leaves
(1, 1, 'ANNUAL', '2025-01-10', '2025-01-11', N'Nghỉ phép năm', 'APPROVED', 6, '2025-01-05', 'admin', '2025-01-04'),
(2, 2, 'ANNUAL', '2025-01-15', '2025-01-15', N'Việc gia đình', 'APPROVED', 6, '2025-01-10', 'it.manager', '2025-01-08'),
(3, 10, 'ANNUAL', '2025-01-08', '2025-01-09', N'Nghỉ phép', 'APPROVED', 6, '2025-01-05', 'sales.manager', '2025-01-03'),
(4, 11, 'ANNUAL', '2025-02-14', '2025-02-14', N'Nghỉ tết', 'APPROVED', 10, '2025-01-20', 'sales.senior', '2025-01-18'),

-- Approved sick leaves
(5, 4, 'SICK', '2025-01-20', '2025-01-20', N'Ốm', 'APPROVED', 2, '2025-01-19', 'dev2', '2025-01-19'),
(6, 13, 'SICK', '2025-01-25', '2025-01-26', N'Bệnh', 'APPROVED', 10, '2025-01-24', 'sales.manager', '2025-01-24'),

-- Pending annual leaves
(7, 8, 'ANNUAL', '2025-02-10', '2025-02-11', N'Du lịch', 'PENDING', NULL, NULL, 'hr.staff2', '2025-02-01'),
(8, 12, 'ANNUAL', '2025-02-15', '2025-02-15', N'Nghỉ phép', 'PENDING', NULL, NULL, 'sales1', '2025-02-05'),
(9, 20, 'ANNUAL', '2025-02-20', '2025-02-21', N'Việc cá nhân', 'PENDING', NULL, NULL, 'marketing1', '2025-02-10'),

-- Pending unpaid leave
(10, 14, 'UNPAID', '2025-02-18', '2025-02-19', N'Việc gia đình khẩn', 'PENDING', NULL, NULL, 'sales3', '2025-02-12'),

-- Rejected leaves
(11, 7, 'ANNUAL', '2025-01-22', '2025-01-24', N'Du lịch', 'REJECTED', 6, '2025-01-20', 'hr.staff1', '2025-01-18'),
(12, 17, 'ANNUAL', '2025-01-28', '2025-01-30', N'Đi chơi', 'REJECTED', 19, '2025-01-25', 'marketing1', '2025-01-22'),

-- Cancelled leave
(13, 16, 'ANNUAL', '2025-02-05', '2025-02-06', N'Nghỉ phép', 'CANCELLED', NULL, NULL, 'accountant1', '2025-01-28'),

-- Approved unpaid leave
(14, 8, 'UNPAID', '2025-01-12', '2025-01-13', N'Công việc cá nhân', 'APPROVED', 6, '2025-01-10', 'hr.staff2', '2025-01-08'),

-- Pending sick leave
(15, 21, 'SICK', '2025-02-17', '2025-02-17', N'Đau răng', 'PENDING', NULL, NULL, 'marketing2', '2025-02-17');

SET IDENTITY_INSERT leave_requests OFF;
GO

-- ============================================
-- 12. ATTENDANCE (January 2025 - 15 working days)
-- ============================================

DECLARE @date DATE = '2025-01-02'; -- Start from Jan 2 (Thu)
DECLARE @employee_id INT;
DECLARE @day_count INT = 0;

WHILE @day_count < 15
BEGIN
    -- Skip weekends
    IF DATEPART(WEEKDAY, @date) NOT IN (1, 7) -- Not Sunday or Saturday
    BEGIN
        -- Insert attendance for active employees (1-24)
        SET @employee_id = 1;
        WHILE @employee_id <= 24
        BEGIN
            -- Skip resigned employee (25)
            IF @employee_id <> 25
            BEGIN
                DECLARE @check_in TIME(0);
                DECLARE @check_out TIME(0);
                DECLARE @status NVARCHAR(20);
                DECLARE @work_hours DECIMAL(5,2);
                DECLARE @notes NVARCHAR(500) = NULL;
                
                -- Employee 1 took leave on Jan 10-11
                IF @employee_id = 1 AND @date IN ('2025-01-10', '2025-01-13')
                BEGIN
                    SET @check_in = NULL;
                    SET @check_out = NULL;
                    SET @status = 'ON_LEAVE';
                    SET @work_hours = 0;
                    SET @notes = N'Nghỉ phép năm';
                END
                -- Employee 2 took leave on Jan 15
                ELSE IF @employee_id = 2 AND @date = '2025-01-15'
                BEGIN
                    SET @check_in = NULL;
                    SET @check_out = NULL;
                    SET @status = 'ON_LEAVE';
                    SET @work_hours = 0;
                    SET @notes = N'Nghỉ phép năm';
                END
                -- Employee 4 sick leave on Jan 20
                ELSE IF @employee_id = 4 AND @date = '2025-01-20'
                BEGIN
                    SET @check_in = NULL;
                    SET @check_out = NULL;
                    SET @status = 'SICK_LEAVE';
                    SET @work_hours = 0;
                    SET @notes = N'Nghỉ ốm';
                END
                -- Employee 8 unpaid leave on Jan 12-13
                ELSE IF @employee_id = 8 AND @date IN ('2025-01-12', '2025-01-13')
                BEGIN
                    SET @check_in = NULL;
                    SET @check_out = NULL;
                    SET @status = 'UNPAID_LEAVE';
                    SET @work_hours = 0;
                    SET @notes = N'Nghỉ không lương';
                END
                -- Employee 10 took leave on Jan 8-9
                ELSE IF @employee_id = 10 AND @date IN ('2025-01-08', '2025-01-09')
                BEGIN
                    SET @check_in = NULL;
                    SET @check_out = NULL;
                    SET @status = 'ON_LEAVE';
                    SET @work_hours = 0;
                    SET @notes = N'Nghỉ phép năm';
                END
                -- Random late/early leave patterns
                ELSE IF @employee_id % 7 = 0 AND DATEPART(DAY, @date) % 5 = 0
                BEGIN
                    SET @check_in = '08:25:00'; -- Late
                    SET @check_out = '17:30:00';
                    SET @status = 'LATE_EARLY';
                    SET @work_hours = 8.0;
                    SET @notes = N'Đi muộn';
                END
                -- Normal present
                ELSE
                BEGIN
                    SET @check_in = CASE 
                        WHEN @employee_id % 3 = 0 THEN '08:05:00'
                        ELSE '08:00:00'
                    END;
                    SET @check_out = CASE 
                        WHEN @employee_id % 5 = 0 THEN '17:15:00'
                        ELSE '17:30:00'
                    END;
                    SET @status = 'PRESENT';
                    SET @work_hours = CASE 
                        WHEN @employee_id % 5 = 0 THEN 8.0
                        ELSE 8.5
                    END;
                END;
                
                INSERT INTO attendance (employee_id, date, check_in, check_out, status, work_hours, notes, created_by, created_date)
                VALUES (@employee_id, @date, @check_in, @check_out, @status, @work_hours, @notes, 'SYSTEM', GETDATE());
            END;
            
            SET @employee_id = @employee_id + 1;
        END;
        
        SET @day_count = @day_count + 1;
    END;
    
    SET @date = DATEADD(DAY, 1, @date);
END;
GO

-- ============================================
-- 13. PAYROLL (December 2024 - PAID & January 2025 - APPROVED)
-- ============================================
SET IDENTITY_INSERT payroll ON;

-- December 2024 - PAID (for all active employees)
INSERT INTO payroll (id, employee_id, month, year, standard_days, work_days, sick_days, unpaid_days,
                    base_salary, overtime_pay, allowances, bonus, 
                    absences_deduction, insurance_deduction, tax_deduction,
                    gross_salary, net_salary, status, payment_date, approved_by, approved_date, created_by, created_date) VALUES
-- Management level
(1, 1, 12, 2024, 26, 24, 0, 0, 50000000, 0, 2000000, 5000000, 0, 6500000, 2500000, 57000000, 48000000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(2, 2, 12, 2024, 26, 25, 0, 0, 30000000, 0, 1500000, 3000000, 0, 3900000, 1200000, 34500000, 29400000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(3, 6, 12, 2024, 26, 26, 0, 0, 30000000, 0, 1500000, 2000000, 0, 3900000, 1200000, 33500000, 28400000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(4, 10, 12, 2024, 26, 26, 0, 0, 30000000, 0, 1500000, 10000000, 0, 3900000, 3500000, 41500000, 34100000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(5, 15, 12, 2024, 26, 26, 0, 0, 35000000, 0, 1500000, 0, 0, 4550000, 1800000, 36500000, 30150000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(6, 19, 12, 2024, 26, 26, 0, 0, 30000000, 0, 1500000, 0, 0, 3900000, 1200000, 31500000, 26400000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(7, 23, 12, 2024, 26, 20, 0, 0, 30000000, 0, 1500000, 0, 0, 3900000, 1200000, 31500000, 26400000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),

-- Senior level
(8, 3, 12, 2024, 26, 26, 0, 0, 20000000, 500000, 1000000, 0, 0, 2600000, 600000, 21500000, 18300000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(9, 11, 12, 2024, 26, 25, 0, 0, 20000000, 0, 1000000, 5000000, 0, 2600000, 1500000, 26000000, 21900000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),

-- Regular staff
(10, 4, 12, 2024, 26, 24, 1, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(11, 7, 12, 2024, 26, 25, 0, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(12, 8, 12, 2024, 26, 24, 0, 2, 15000000, 0, 1000000, 0, 1153846, 1950000, 300000, 16000000, 12596154, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(13, 12, 12, 2024, 26, 26, 0, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(14, 13, 12, 2024, 26, 21, 2, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(15, 16, 12, 2024, 26, 26, 0, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(16, 17, 12, 2024, 26, 25, 0, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(17, 20, 12, 2024, 26, 26, 0, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(18, 21, 12, 2024, 26, 24, 0, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(19, 24, 12, 2024, 26, 20, 0, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),

-- Junior staff
(20, 9, 12, 2024, 26, 20, 0, 0, 12000000, 0, 800000, 0, 0, 1560000, 200000, 12800000, 11040000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(21, 14, 12, 2024, 26, 22, 0, 0, 12000000, 0, 800000, 0, 0, 1560000, 200000, 12800000, 11040000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(22, 18, 12, 2024, 26, 18, 0, 0, 12000000, 0, 800000, 0, 0, 1560000, 200000, 12800000, 11040000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),
(23, 22, 12, 2024, 26, 20, 0, 0, 12000000, 0, 800000, 0, 0, 1560000, 200000, 12800000, 11040000, 'PAID', '2025-01-05', 1, '2025-01-03', 'SYSTEM', '2025-01-02'),

-- January 2025 - APPROVED (sample for first 10 employees)
(24, 1, 1, 2025, 26, 13, 0, 0, 50000000, 0, 2000000, 0, 0, 6500000, 2500000, 52000000, 43000000, 'APPROVED', NULL, 1, '2025-02-03', 'SYSTEM', '2025-02-02'),
(25, 2, 1, 2025, 26, 14, 0, 0, 30000000, 0, 1500000, 0, 0, 3900000, 1200000, 31500000, 26400000, 'APPROVED', NULL, 1, '2025-02-03', 'SYSTEM', '2025-02-02'),
(26, 3, 1, 2025, 26, 15, 0, 0, 20000000, 0, 1000000, 0, 0, 2600000, 600000, 21000000, 17800000, 'APPROVED', NULL, 1, '2025-02-03', 'SYSTEM', '2025-02-02'),
(27, 4, 1, 2025, 26, 14, 1, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'APPROVED', NULL, 1, '2025-02-03', 'SYSTEM', '2025-02-02'),
(28, 6, 1, 2025, 26, 15, 0, 0, 30000000, 0, 1500000, 0, 0, 3900000, 1200000, 31500000, 26400000, 'APPROVED', NULL, 1, '2025-02-03', 'SYSTEM', '2025-02-02'),
(29, 7, 1, 2025, 26, 15, 0, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'APPROVED', NULL, 1, '2025-02-03', 'SYSTEM', '2025-02-02'),
(30, 8, 1, 2025, 26, 13, 0, 2, 15000000, 0, 1000000, 0, 1153846, 1950000, 300000, 16000000, 12596154, 'APPROVED', NULL, 1, '2025-02-03', 'SYSTEM', '2025-02-02'),
(31, 10, 1, 2025, 26, 13, 0, 0, 30000000, 0, 1500000, 0, 0, 3900000, 1200000, 31500000, 26400000, 'APPROVED', NULL, 1, '2025-02-03', 'SYSTEM', '2025-02-02'),
(32, 11, 1, 2025, 26, 14, 0, 0, 20000000, 0, 1000000, 0, 0, 2600000, 600000, 21000000, 17800000, 'APPROVED', NULL, 1, '2025-02-03', 'SYSTEM', '2025-02-02'),
(33, 12, 1, 2025, 26, 15, 0, 0, 15000000, 0, 1000000, 0, 0, 1950000, 300000, 16000000, 13750000, 'APPROVED', NULL, 1, '2025-02-03', 'SYSTEM', '2025-02-02');

SET IDENTITY_INSERT payroll OFF;
GO

-- ============================================
-- EXTRA -> XÂY TIẾP
-- ============================================

-- Dùng cho tạo employee code tự động
DECLARE @maxId INT;
SELECT @maxId = ISNULL(MAX(id), 0) FROM employees;
DECLARE @sql NVARCHAR(MAX);
SET @sql = N'CREATE SEQUENCE Seq_EmployeeCode START WITH ' + CAST(@maxId + 1 AS NVARCHAR(10)) + N' INCREMENT BY 1';
EXEC sp_executesql @sql;
-- lệnh gọi sequence tiếp theo và +1 vào sequence: SELECT NEXT VALUE FOR Seq_EmployeeCode
-- bản để restart lại số đếm của sequence: ALTER SEQUENCE Seq_EmployeeCode RESTART WITH 1;
SELECT current_value FROM sys.sequences WHERE name = 'Seq_EmployeeCode';

-- Bảng lưu các thiết lập hệ thống
CREATE TABLE system_settings (
    setting_key NVARCHAR(50) PRIMARY KEY, 
    setting_value NVARCHAR(255) NOT NULL, 
    description NVARCHAR(255),            
    updated_at DATETIME DEFAULT GETDATE(),
    updated_by NVARCHAR(50)
);
GO
INSERT INTO system_settings (setting_key, setting_value, description, updated_by) VALUES 
('ANNUAL_LEAVE_DAYS', '12', N'Số ngày phép năm mặc định', 'SYSTEM'),
('SICK_LEAVE_DAYS', '12', N'Số ngày phép nghỉ ốm năm mặc định', 'SYSTEM'),
('LAST_GEN_YEAR', '2025', N'Năm gần nhất thực hiện reset mã nhân viên', 'SYSTEM'),
('DEFAULT_PAGE_SIZE', '10', N'Số lượng bản ghi mặc định trên mỗi trang (phân trang)', 'SYSTEM');
GO

