-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Apr 07, 2026 at 04:54 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tec_sis`
--

-- --------------------------------------------------------

--
-- Table structure for table `courses`
--

CREATE TABLE `courses` (
                           `id` int(11) NOT NULL,
                           `course_code` varchar(10) NOT NULL,
                           `course_name` varchar(100) NOT NULL,
                           `credits` int(11) NOT NULL,
                           `total_hours` int(11) NOT NULL,
                           `session_type` enum('THEORY','PRACTICAL','BOTH') NOT NULL DEFAULT 'THEORY',
                           `department_id` int(11) NOT NULL,
                           `lecturer_in_charge_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `courses`
--

INSERT INTO `courses` (`id`, `course_code`, `course_name`, `credits`, `total_hours`, `session_type`, `department_id`, `lecturer_in_charge_id`) VALUES
                                                                                                                                                   (1, 'ICT2152', 'E-Commerce Implementation, Management and Security', 2, 30, 'THEORY', 2, 3),
                                                                                                                                                   (2, 'ICT2142', 'Object Oriented Analysis and Design', 2, 30, 'THEORY', 2, 4),
                                                                                                                                                   (3, 'ICT2132', 'Object Oriented Programming Practicum', 2, 60, 'PRACTICAL', 2, 5),
                                                                                                                                                   (4, 'ICT2122', 'Object Oriented Programming', 2, 30, 'THEORY', 2, 5),
                                                                                                                                                   (5, 'ICT2113', 'Data Structures and Algorithms', 3, 45, 'BOTH', 2, 6),
                                                                                                                                                   (6, 'ENG2122', 'English III', 2, 30, 'THEORY', 4, 7),
                                                                                                                                                   (7, 'TCS2122', 'Soft Skills', 2, 30, 'THEORY', 4, 3),
                                                                                                                                                   (8, 'TCS2112', 'Business Economics', 2, 30, 'THEORY', 4, 4);

-- --------------------------------------------------------

--
-- Table structure for table `departments`
--

CREATE TABLE `departments` (
                               `id` int(11) NOT NULL,
                               `dept_code` varchar(10) NOT NULL,
                               `dept_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `departments`
--

INSERT INTO `departments` (`id`, `dept_code`, `dept_name`) VALUES
                                                               (1, 'ET', 'Engineering Technology'),
                                                               (2, 'ICT', 'Information and Communication Technology'),
                                                               (3, 'BST', 'Biosystems Technology'),
                                                               (4, 'MS', 'Multidisciplinary Studies');

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
                         `user_id` int(11) NOT NULL,
                         `staff_code` varchar(20) NOT NULL,
                         `designation` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`user_id`, `staff_code`, `designation`) VALUES
                                                                 (2, 'DEAN01', 'Faculty Dean'),
                                                                 (3, 'LEC001', 'Senior Lecturer'),
                                                                 (4, 'LEC002', 'Lecturer'),
                                                                 (5, 'LEC003', 'Senior Lecturer'),
                                                                 (6, 'LEC004', 'Lecturer (Probationary)'),
                                                                 (7, 'LEC005', 'Senior Lecturer'),
                                                                 (8, 'TO001', 'Technical Officer Grade I'),
                                                                 (9, 'TO002', 'Technical Officer Grade II'),
                                                                 (10, 'TO003', 'Technical Officer Grade I'),
                                                                 (11, 'TO004', 'Technical Officer Grade II'),
                                                                 (12, 'TO005', 'Technical Officer Grade III');

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
                           `user_id` int(11) NOT NULL,
                           `registration_no` varchar(20) NOT NULL,
                           `registration_year` year(4) NOT NULL,
                           `student_type` enum('PROPER','REPEAT') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`user_id`, `registration_no`, `registration_year`, `student_type`) VALUES
                                                                                              (13, 'TG/2022/001', '2022', 'PROPER'),
                                                                                              (14, 'TG/2022/002', '2022', 'PROPER'),
                                                                                              (15, 'TG/2022/003', '2022', 'PROPER'),
                                                                                              (16, 'TG/2022/004', '2022', 'PROPER'),
                                                                                              (17, 'TG/2022/005', '2022', 'PROPER'),
                                                                                              (18, 'TG/2022/006', '2022', 'PROPER'),
                                                                                              (19, 'TG/2022/011', '2022', 'PROPER'),
                                                                                              (20, 'TG/2022/012', '2022', 'PROPER'),
                                                                                              (21, 'TG/2022/013', '2022', 'PROPER'),
                                                                                              (22, 'TG/2022/014', '2022', 'PROPER'),
                                                                                              (23, 'TG/2021/080', '2021', 'REPEAT'),
                                                                                              (24, 'TG/2021/081', '2021', 'REPEAT'),
                                                                                              (25, 'TG/2021/082', '2021', 'REPEAT'),
                                                                                              (26, 'TG/2021/085', '2021', 'REPEAT'),
                                                                                              (27, 'TG/2021/090', '2021', 'REPEAT');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                         `id` int(11) NOT NULL,
                         `first_name` varchar(50) NOT NULL,
                         `last_name` varchar(50) NOT NULL,
                         `email` varchar(100) NOT NULL,
                         `password_hash` varchar(255) NOT NULL,
                         `phone` varchar(15) NOT NULL,
                         `address` varchar(150) DEFAULT NULL,
                         `dob` date NOT NULL,
                         `role` enum('ADMIN','DEAN','LECTURER','TO','STUDENT') NOT NULL,
                         `status` enum('ACTIVE','BLOCKED','SUSPENDED') DEFAULT 'ACTIVE',
                         `department_id` int(11) NOT NULL,
                         `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
                         `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `first_name`, `last_name`, `email`, `password_hash`, `phone`, `address`, `dob`, `role`, `status`, `department_id`, `created_at`, `updated_at`) VALUES
                                                                                                                                                                              (1, 'Saman', 'Kumara', 'test@gmail.com', '1234', '0711111111', NULL, '1985-05-10', 'ADMIN', 'ACTIVE', 1, '2026-04-04 02:22:59', '2026-04-04 02:49:31'),
                                                                                                                                                                              (2, 'Sunil', 'Shantha', 'dean@tec.ruh.ac.lk', 'hash_dean', '0712222222', NULL, '1970-01-20', 'DEAN', 'ACTIVE', 1, '2026-04-04 02:22:59', '2026-04-04 02:22:59'),
                                                                                                                                                                              (3, 'Kamal', 'Gunasekara', 'kamal@tec.ruh.ac.lk', 'hash_l1', '0713333333', NULL, '1980-02-15', 'LECTURER', 'ACTIVE', 1, '2026-04-04 02:24:25', '2026-04-04 02:24:25'),
                                                                                                                                                                              (4, 'Nimal', 'Perera', 'nimal@tec.ruh.ac.lk', 'hash_l2', '0714444444', NULL, '1982-06-25', 'LECTURER', 'ACTIVE', 1, '2026-04-04 02:24:25', '2026-04-04 02:24:25'),
                                                                                                                                                                              (5, 'Ruwan', 'Silva', 'ruwan@tec.ruh.ac.lk', 'hash_l3', '0715555555', NULL, '1978-11-05', 'LECTURER', 'ACTIVE', 1, '2026-04-04 02:24:25', '2026-04-04 02:24:25'),
                                                                                                                                                                              (6, 'Amara', 'Siriwardena', 'amara@tec.ruh.ac.lk', 'hash_l4', '0716666666', NULL, '1985-03-12', 'LECTURER', 'ACTIVE', 1, '2026-04-04 02:24:25', '2026-04-04 02:24:25'),
                                                                                                                                                                              (7, 'Kasun', 'Rajapaksha', 'kasun@tec.ruh.ac.lk', 'hash_l5', '0717777777', NULL, '1988-09-30', 'LECTURER', 'ACTIVE', 1, '2026-04-04 02:24:25', '2026-04-04 02:24:25'),
                                                                                                                                                                              (8, 'Jagath', 'Priyanta', 'jagath@tec.ruh.ac.lk', 'hash_to1', '0718888888', NULL, '1990-04-10', 'TO', 'ACTIVE', 1, '2026-04-04 02:24:45', '2026-04-04 02:24:45'),
                                                                                                                                                                              (9, 'Piyal', 'Nishantha', 'piyal@tec.ruh.ac.lk', 'hash_to2', '0719999999', NULL, '1992-08-14', 'TO', 'ACTIVE', 1, '2026-04-04 02:24:45', '2026-04-04 02:24:45'),
                                                                                                                                                                              (10, 'Sajith', 'Premadasa', 'sajith@tec.ruh.ac.lk', 'hash_to3', '0701111111', NULL, '1989-12-01', 'TO', 'ACTIVE', 1, '2026-04-04 02:24:45', '2026-04-04 02:24:45'),
                                                                                                                                                                              (11, 'Bandula', 'Gunawardena', 'bandula@tec.ruh.ac.lk', 'hash_to4', '0702222222', NULL, '1991-05-22', 'TO', 'ACTIVE', 1, '2026-04-04 02:24:45', '2026-04-04 02:24:45'),
                                                                                                                                                                              (12, 'Mahinda', 'Yapa', 'mahinda@tec.ruh.ac.lk', 'hash_to5', '0703333333', NULL, '1993-01-18', 'TO', 'ACTIVE', 1, '2026-04-04 02:24:45', '2026-04-04 02:24:45'),
                                                                                                                                                                              (13, 'Aruni', 'Madushani', 'aruni@fot.ruh.ac.lk', 'pass123', '0771000001', 'Matara', '2002-01-15', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (14, 'Bawantha', 'Deepal', 'bawantha@fot.ruh.ac.lk', 'pass123', '0771000002', 'Galle', '2002-02-20', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (15, 'Chamindu', 'Dilshan', 'chamindu@fot.ruh.ac.lk', 'pass123', '0771000003', 'Hambantota', '2002-03-10', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (16, 'Dhanushka', 'Lakmal', 'dhanushka@fot.ruh.ac.lk', 'pass123', '0771000004', 'Colombo', '2002-04-05', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (17, 'Eranga', 'Prasad', 'eranga@fot.ruh.ac.lk', 'pass123', '0771000005', 'Kandy', '2002-05-12', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (18, 'Fathima', 'Rizna', 'rizna@fot.ruh.ac.lk', 'pass123', '0771000006', 'Matara', '2002-06-18', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (19, 'Gayan', 'Suranga', 'gayan@fot.ruh.ac.lk', 'pass123', '0771000007', 'Galle', '2002-07-25', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (20, 'Harsha', 'de Silva', 'harsha@fot.ruh.ac.lk', 'pass123', '0771000008', 'Ambalangoda', '2002-08-30', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (21, 'Ishara', 'Sandamini', 'ishara@fot.ruh.ac.lk', 'pass123', '0771000009', 'Hakmana', '2002-09-14', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (22, 'Janith', 'Kavinda', 'janith_k@fot.ruh.ac.lk', 'pass123', '0771000010', 'Matara', '2002-10-22', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (23, 'Kavindi', 'Nethmini', 'kavindi@fot.ruh.ac.lk', 'pass123', '0771000011', 'Dickwella', '2001-11-05', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (24, 'Lahiru', 'Sampath', 'lahiru@fot.ruh.ac.lk', 'pass123', '0771000012', 'Weligama', '2001-12-12', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (25, 'Maneesha', 'Udayangani', 'maneesha@fot.ruh.ac.lk', 'pass123', '0771000013', 'Matara', '2001-01-30', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (26, 'Niroshan', 'Bandara', 'niroshan@fot.ruh.ac.lk', 'pass123', '0771000014', 'Galle', '2001-03-15', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28'),
                                                                                                                                                                              (27, 'Yohan', 'Perera', 'yohan@fot.ruh.ac.lk', 'pass123', '0771000015', 'Matara', '2001-05-05', 'STUDENT', 'ACTIVE', 1, '2026-04-04 02:30:28', '2026-04-04 02:30:28');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `courses`
--
ALTER TABLE `courses`
    ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `course_code` (`course_code`),
  ADD KEY `department_id` (`department_id`),
  ADD KEY `lecturer_in_charge_id` (`lecturer_in_charge_id`);

--
-- Indexes for table `departments`
--
ALTER TABLE `departments`
    ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `dept_code` (`dept_code`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
    ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `staff_code` (`staff_code`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
    ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `registration_no` (`registration_no`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
    ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `phone` (`phone`),
  ADD KEY `department_id` (`department_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `courses`
--
ALTER TABLE `courses`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `departments`
--
ALTER TABLE `departments`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `courses`
--
ALTER TABLE `courses`
    ADD CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `courses_ibfk_2` FOREIGN KEY (`lecturer_in_charge_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `staff`
--
ALTER TABLE `staff`
    ADD CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `student`
--
ALTER TABLE `student`
    ADD CONSTRAINT `student_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `users`
--
ALTER TABLE `users`
    ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
