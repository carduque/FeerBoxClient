package com.feerbox.client;

import java.util.List;

import org.junit.Test;

import com.feerbox.client.model.Weather;
import com.feerbox.client.services.WeatherService;

public class WeatherTest {
	@Test
	public void weatherTest(){
		List<Weather> list = WeatherService.notUploaded();
		if(list!=null && list.size()!=0){
			int i=1;
			for(Weather weather: list){
				boolean ok = WeatherService.saveServer(weather);
				if(ok){
					WeatherService.uploaded(weather);
				}
				i++;
			}
		}
	}
}
