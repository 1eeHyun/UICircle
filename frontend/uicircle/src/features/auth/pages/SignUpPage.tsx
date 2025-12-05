import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signup } from "../services/AuthService";

const SignUpPage = () => {
  const navigate = useNavigate();
  
  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
    firstName: "",
    middleName: "",
    lastName: "",
    phoneNumber: "",
  });
  
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [loading, setLoading] = useState(false);
  const [touched, setTouched] = useState<{ [key: string]: boolean }>({});

  const validateField = (name: string, value: string) => {
    let error = "";
    
    switch (name) {
      case "username":
        if (!value) error = "Username is required";
        else if (value.length < 6 || value.length > 14) 
          error = "Username must be 6-14 characters";
        break;
      case "email":
        if (!value) error = "Email is required";
        else if (!/^[A-Za-z0-9._%+-]+@uic\.edu$/.test(value))
          error = "Must be a valid UIC email address";
        break;
      case "password":
        if (!value) error = "Password is required";
        else if (value.length < 8) error = "Password must be at least 8 characters";
        else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value))
          error = "Password must contain uppercase, lowercase, and number";
        break;
      case "confirmPassword":
        if (!value) error = "Please confirm your password";
        else if (value !== form.password) error = "Passwords do not match";
        break;
      case "firstName":
        if (!value) error = "First name is required";
        else if (value.length > 50) error = "First name must not exceed 50 characters";
        break;
      case "lastName":
        if (!value) error = "Last name is required";
        else if (value.length > 50) error = "Last name must not exceed 50 characters";
        break;
      case "middleName":
        if (value && value.length > 50) error = "Middle name must not exceed 50 characters";
        break;
      case "phoneNumber":
        if (value && !/^(\+1\s?)?(\(?\d{3}\)?[\s.-]?)\d{3}[\s.-]?\d{4}$/.test(value)) {
          error = "Please enter a valid phone number";
        }
        break;
    }
    
    return error;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });

    if (touched[name]) {
      const error = validateField(name, value);
      setErrors({ ...errors, [name]: error });
    }
  };

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setTouched({ ...touched, [name]: true });
    const error = validateField(name, value);
    setErrors({ ...errors, [name]: error });
  };

  const validateForm = () => {
    const newErrors: { [key: string]: string } = {};
    const fields = ["username", "email", "password", "confirmPassword", "firstName", "lastName"];
    
    fields.forEach(field => {
      const error = validateField(field, form[field as keyof typeof form]);
      if (error) newErrors[field] = error;
    });

    if (form.middleName) {
      const error = validateField("middleName", form.middleName);
      if (error) newErrors.middleName = error;
    }
    if (form.phoneNumber) {
      const error = validateField("phoneNumber", form.phoneNumber);
      if (error) newErrors.phoneNumber = error;
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      setLoading(true);
      const { confirmPassword, ...signupData } = form;
      await signup({
        ...signupData,
        middleName: signupData.middleName || undefined,
        phoneNumber: signupData.phoneNumber || undefined,
      });
      
      navigate("/verify-email/pending");
    } catch (err: any) {
      const errorMessage = err?.response?.data?.message || "Signup failed. Please try again.";
      setErrors({ ...errors, submit: errorMessage });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-surface-light to-gray-100 flex items-center justify-center p-4 py-12">
      <div className="w-full max-w-4xl">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-5xl font-bold text-primary mb-3">UICircle</h1>
          <h2 className="text-2xl font-semibold text-gray-800 mb-2">Create Your Account</h2>
          <p className="text-gray-600">
            Already have an account?{" "}
            <button
              type="button"
              onClick={() => navigate("/")}
              className="text-primary hover:text-primary-dark font-semibold hover:underline transition"
            >
              Sign In
            </button>
          </p>
        </div>

        {/* Form Card */}
        <div className="bg-background-light rounded-2xl shadow-2xl p-8 border border-border-light">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Account Information */}
            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <span className="w-8 h-8 bg-primary/10 text-primary rounded-full flex items-center justify-center text-sm font-bold">1</span>
                Account Information
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">
                    Username <span className="text-primary">*</span>
                  </label>
                  <input
                    name="username"
                    type="text"
                    value={form.username}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`w-full px-4 py-2.5 bg-surface-light border rounded-xl focus:bg-background-light focus:ring-2 outline-none transition ${
                      touched.username && errors.username 
                        ? "border-red-300 focus:border-red-500 focus:ring-red-200" 
                        : "border-border-light focus:border-primary focus:ring-primary/20"
                    }`}
                    placeholder="Choose a username"
                  />
                  {touched.username && errors.username && (
                    <p className="mt-1.5 text-xs text-red-600">{errors.username}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">
                    Email <span className="text-primary">*</span>
                  </label>
                  <input
                    name="email"
                    type="email"
                    value={form.email}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`w-full px-4 py-2.5 bg-surface-light border rounded-xl focus:bg-background-light focus:ring-2 outline-none transition ${
                      touched.email && errors.email 
                        ? "border-red-300 focus:border-red-500 focus:ring-red-200" 
                        : "border-border-light focus:border-primary focus:ring-primary/20"
                    }`}
                    placeholder="Email@uic.edu"
                  />
                  {touched.email && errors.email && (
                    <p className="mt-1.5 text-xs text-red-600">{errors.email}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">
                    Password <span className="text-primary">*</span>
                  </label>
                  <input
                    name="password"
                    type="password"
                    value={form.password}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`w-full px-4 py-2.5 bg-surface-light border rounded-xl focus:bg-background-light focus:ring-2 outline-none transition ${
                      touched.password && errors.password 
                        ? "border-red-300 focus:border-red-500 focus:ring-red-200" 
                        : "border-border-light focus:border-primary focus:ring-primary/20"
                    }`}
                    placeholder="••••••••"
                  />
                  {touched.password && errors.password && (
                    <p className="mt-1.5 text-xs text-red-600">{errors.password}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">
                    Confirm Password <span className="text-primary">*</span>
                  </label>
                  <input
                    name="confirmPassword"
                    type="password"
                    value={form.confirmPassword}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`w-full px-4 py-2.5 bg-surface-light border rounded-xl focus:bg-background-light focus:ring-2 outline-none transition ${
                      touched.confirmPassword && errors.confirmPassword 
                        ? "border-red-300 focus:border-red-500 focus:ring-red-200" 
                        : "border-border-light focus:border-primary focus:ring-primary/20"
                    }`}
                    placeholder="••••••••"
                  />
                  {touched.confirmPassword && errors.confirmPassword && (
                    <p className="mt-1.5 text-xs text-red-600">{errors.confirmPassword}</p>
                  )}
                </div>
              </div>
            </div>

            {/* Personal Information */}
            <div className="pt-4 border-t border-border-light">
              <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <span className="w-8 h-8 bg-primary/10 text-primary rounded-full flex items-center justify-center text-sm font-bold">2</span>
                Personal Information
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">
                    First Name <span className="text-primary">*</span>
                  </label>
                  <input
                    name="firstName"
                    type="text"
                    value={form.firstName}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`w-full px-4 py-2.5 bg-surface-light border rounded-xl focus:bg-background-light focus:ring-2 outline-none transition ${
                      touched.firstName && errors.firstName 
                        ? "border-red-300 focus:border-red-500 focus:ring-red-200" 
                        : "border-border-light focus:border-primary focus:ring-primary/20"
                    }`}
                    placeholder="First name"
                  />
                  {touched.firstName && errors.firstName && (
                    <p className="mt-1.5 text-xs text-red-600">{errors.firstName}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">
                    Middle Name <span className="text-gray-400 text-xs">(Optional)</span>
                  </label>
                  <input
                    name="middleName"
                    type="text"
                    value={form.middleName}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className="w-full px-4 py-2.5 bg-surface-light border border-border-light rounded-xl focus:bg-background-light focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition"
                    placeholder="Middle name"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">
                    Last Name <span className="text-primary">*</span>
                  </label>
                  <input
                    name="lastName"
                    type="text"
                    value={form.lastName}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`w-full px-4 py-2.5 bg-surface-light border rounded-xl focus:bg-background-light focus:ring-2 outline-none transition ${
                      touched.lastName && errors.lastName 
                        ? "border-red-300 focus:border-red-500 focus:ring-red-200" 
                        : "border-border-light focus:border-primary focus:ring-primary/20"
                    }`}
                    placeholder="Last name"
                  />
                  {touched.lastName && errors.lastName && (
                    <p className="mt-1.5 text-xs text-red-600">{errors.lastName}</p>
                  )}
                </div>

                <div className="md:col-span-3">
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">
                    Phone Number <span className="text-primary">*</span>
                  </label>
                  <input
                    name="phoneNumber"
                    type="text"
                    value={form.phoneNumber}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className="w-full px-4 py-2.5 bg-surface-light border border-border-light rounded-xl focus:bg-background-light focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition"
                    placeholder=""
                  />
                  {touched.phoneNumber && errors.phoneNumber && (
                    <p className="mt-1.5 text-xs text-red-600">{errors.phoneNumber}</p>
                  )}
                </div>
              </div>
            </div>

            {/* Submit Error */}
            {errors.submit && (
              <div className="bg-red-50 border border-red-200 rounded-xl p-4">
                <p className="text-red-700 text-sm">{errors.submit}</p>
              </div>
            )}

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-4 bg-gradient-to-r from-primary to-primary-dark hover:from-primary-dark hover:to-primary-darker text-background-light rounded-xl font-semibold shadow-lg shadow-primary/30 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 transform hover:scale-[1.01]"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="animate-spin h-5 w-5" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Creating Account...
                </span>
              ) : (
                "Create Account"
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export { SignUpPage };