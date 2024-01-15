package com.api.siiconges.schema.response.user;

import java.util.HashSet;
import java.util.Set;

import com.api.siiconges.model.security.Role;


public class UserAdminResponse {

	private Long id;
	private String firstname;
	private String lastname;
	private String email;
	private Set<Role> roles = new HashSet<>();

	public UserAdminResponse(Long id, String firstname, String lastname, String email, Set<Role> roles) {
		super();
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "UserAdminResponse [id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + ", email="
				+ email + ", roles=" + roles + "]";
	}

}
