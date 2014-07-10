<#-- Copyright © Capgemini 2013. All rights reserved. -->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
                      http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
                      http://www.springframework.org/schema/context
                      http://www.springframework.org/schema/context/spring-context-3.0.xsd">

	<context:annotation-config />

	<bean id="abstractLayerImpl"
		class="${variables.rootPackage}.common.AbstractLayerImpl">
		<property name="dozer" ref="dozer" />
	</bean>

</beans>
