--navdetails:

CREATE TABLE `navdetails` (
  `scheme_code` int(11) NOT NULL,
  `scheme_name` varchar(200) NOT NULL,
  `isin` varchar(40) NOT NULL,
  `nav` double NOT NULL,
  `date` datetime NOT NULL,
  `custom_scheme_name` varchar(100) DEFAULT NULL,
  `units_held` double DEFAULT NULL,
  `amount_invested` double DEFAULT NULL,
  `current_value` double DEFAULT NULL,
  PRIMARY KEY (`scheme_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--investment_schedule:
CREATE TABLE `investment_schedule` (
  `scheme_code` varchar(12) NOT NULL,
  `batch` int(11) NOT NULL,
  `investment_amount` double NOT NULL,
  `day` int(11) NOT NULL,
  PRIMARY KEY (`scheme_code`,`batch`,`investment_amount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
