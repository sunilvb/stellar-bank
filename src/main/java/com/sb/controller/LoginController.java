package com.sb.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sb.model.Account;
import com.sb.model.Transfer;
import com.sb.model.User;
import com.sb.service.AccountService;
import com.sb.service.UserService;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private static HttpServletRequest request;

	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user",
					"There is already a user registered with the email provided");
		}
		if (bindingResult.hasErrors()) {
			System.out.println("There was an error...");
			modelAndView.setViewName("registration");
		} else {
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User registered successfully. Please login.");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("login");

		}
		return modelAndView;
	}

	@RequestMapping(value = "/admin/home", method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.addObject("clientip", getClientIp());
		float total = accountService.getTotalBalance(user.getEmail());
		if (total < 0)
			total = 0;
		modelAndView.addObject("total", total);
		modelAndView.addObject("network", accountService.getNetwork());
		modelAndView.addObject("transfer", new Transfer());
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}

	@RequestMapping(value = "/admin/home", method = RequestMethod.POST)
	public ModelAndView home2(Transfer transfer) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.addObject("clientip", getClientIp());
		modelAndView.addObject("transfer", transfer);
		modelAndView.setViewName("admin/send");
		return modelAndView;
	}

	@RequestMapping(value = "/admin/send", method = RequestMethod.POST)
	public ModelAndView send(Transfer transfer) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.addObject("clientip", getClientIp());
		accountService.transferFunds(accountService.findByEmail(user.getEmail()).get(0).getPrivateKey(),
				transfer.getTo(), transfer.getAmount(), transfer.getMemo());

		modelAndView.setViewName("admin/send");
		return modelAndView;
	}

	@RequestMapping(value = "/admin/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.addObject("clientip", getClientIp());

		modelAndView.addObject("account", new Account());
		modelAndView.setViewName("admin/create");

		return modelAndView;
	}

	@RequestMapping(value = "/admin/create", method = RequestMethod.POST)
	public ModelAndView openAccount(Account account) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.addObject("clientip", getClientIp());
		accountService.openAccount(account.getAccountName(), user.getEmail());
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}

	@RequestMapping(value = "/admin/details", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");

		List<Account> theList = new ArrayList<Account>();
		for (Account acc : accountService.findByEmail(user.getEmail())) {
			acc.setBalance(accountService.getBalance(acc.getPublicKey()));
			theList.add(acc);
		}
		modelAndView.addObject("network", accountService.getNetwork());
		modelAndView.addObject("accounts", theList);
		modelAndView.setViewName("admin/details");

		return modelAndView;
	}

	private static String getClientIp() {

		String remoteAddr = "";

		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}
		System.out.println("Remote Address: " + remoteAddr);
		return remoteAddr;
	}

}
