package com.variance.mimiprotect.contacts;

import com.variance.mimiprotect.util.Settings;

public class UserLoginInformation {
	private String username;
	private String password;
	private String loginClientParameter;
	private double currentLongitude;
	private double currentLatitude;

	public UserLoginInformation() {
		this.loginClientParameter = "cbnapp";
		this.currentLatitude = Settings.getCurrentLatitude();
		this.currentLongitude = Settings.getCurrentLongitude();
	}

	public UserLoginInformation(String username, String password) {
		this();
		this.username = username;
		this.password = password;
	}

	public UserLoginInformation(String username, String password,
			String loginClientParameter, double currentLongitude,
			double currentLatitude) {
		super();
		this.username = username;
		this.password = password;
		this.loginClientParameter = loginClientParameter;
		this.currentLongitude = currentLongitude;
		this.currentLatitude = currentLatitude;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLoginClientParameter() {
		return loginClientParameter;
	}

	public void setLoginClientParameter(String loginClientParameter) {
		this.loginClientParameter = loginClientParameter;
	}

	public double getCurrentLongitude() {
		return currentLongitude;
	}

	public void setCurrentLongitude(double currentLongitude) {
		this.currentLongitude = currentLongitude;
	}

	public double getCurrentLatitude() {
		return currentLatitude;
	}

	public void setCurrentLatitude(double currentLatitude) {
		this.currentLatitude = currentLatitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(currentLatitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(currentLongitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((loginClientParameter == null) ? 0 : loginClientParameter
						.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserLoginInformation other = (UserLoginInformation) obj;
		if (Double.doubleToLongBits(currentLatitude) != Double
				.doubleToLongBits(other.currentLatitude))
			return false;
		if (Double.doubleToLongBits(currentLongitude) != Double
				.doubleToLongBits(other.currentLongitude))
			return false;
		if (loginClientParameter == null) {
			if (other.loginClientParameter != null)
				return false;
		} else if (!loginClientParameter.equals(other.loginClientParameter))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserLoginInformation [username=" + username + ", password="
				+ password + ", loginClientParameter=" + loginClientParameter
				+ ", currentLongitude=" + currentLongitude
				+ ", currentLatitude=" + currentLatitude + "]";
	}

}
