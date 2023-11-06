package com.project.schoolmanagment.service.validator;

import com.project.schoolmanagment.exeption.ResourceNotFoundException;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class DateTimeValidator {


	//11:35
	//15:30
	public boolean checkTime(LocalTime start,LocalTime stop){
		return start.isAfter(stop) || start.equals(stop);
	}

	public void checkTimeWithException(LocalTime start,LocalTime stop){
		if(checkTime(start,stop)) {
			throw new ResourceNotFoundException(ErrorMessages.TIME_NOT_VALID_MESSAGE);
		}
	}
}
