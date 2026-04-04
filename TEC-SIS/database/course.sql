-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 04, 2026 at 06:00 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

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
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `course_id` int(11) NOT NULL,
  `course_code` varchar(10) NOT NULL,
  `course_name` varchar(100) NOT NULL,
  `credits` int(11) NOT NULL,
  `total_hours` int(11) NOT NULL,
  `session_type` enum('THEORY','PRACTICAL','BOTH') NOT NULL DEFAULT 'THEORY',
  `department_id` int(11) NOT NULL,
  `lecturer_in_charge_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`course_id`, `course_code`, `course_name`, `credits`, `total_hours`, `session_type`, `department_id`, `lecturer_in_charge_id`) VALUES
(11, 'ICT2152', 'E-Commerce Implementation, Management and Security', 3, 45, 'THEORY', 1, 1),
(12, 'ICT2142', 'Object Oriented Analysis and Design', 3, 45, 'THEORY', 1, 2),
(13, 'ICT2132', 'Object Oriented Programming Practicum', 2, 60, 'PRACTICAL', 1, 3),
(14, 'ICT2122', 'Object Oriented Programming', 3, 45, 'BOTH', 1, 3),
(15, 'ICT2113', 'Data Structures and Algorithms', 3, 45, 'THEORY', 1, 4),
(16, 'ENG1222', 'English II', 2, 30, 'THEORY', 2, 5),
(17, 'TCS1212', 'Fundamentals of Management', 2, 30, 'THEORY', 3, 6),
(18, 'ENG2122', 'English III', 2, 30, 'THEORY', 2, 5),
(19, 'TCS2122', 'Soft Skills', 2, 30, 'THEORY', 3, 7),
(20, 'TCS2112', 'Business Economics', 2, 30, 'THEORY', 3, 8);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`course_id`),
  ADD UNIQUE KEY `course_code` (`course_code`),
  ADD KEY `department_id` (`department_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `course`
--
ALTER TABLE `course`
  MODIFY `course_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `course`
--
ALTER TABLE `course`
  ADD CONSTRAINT `course_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
