package com.sb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.sb.model.Role;
import com.sb.repository.RoleRepository;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

	/**
	 * This event is executed as late as conceivably possible to indicate that
	 * the application is ready to service requests.
	 */
	@Autowired
	RoleRepository repo;
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {

		if(repo.findAll().isEmpty())
		{
			repo.save(new Role("ADMIN"));
			repo.save(new Role("USER"));
		}
		return;
	}

} // class