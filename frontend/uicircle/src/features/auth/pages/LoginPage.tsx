import { useState } from "react";
import {useNavigate } from "react-router-dom";
import { validateEmail, validatePassword } from "@/utils/validators";
import { login } from "../services/AuthService";


const LoginPage = () => {

  const navigate = useNavigate();
  

  const [form, setForm] = useState({
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [errors, setErrors] = useState({
    email: "",
    password: "",
    confirmPassword: "",
    submit: ""
  });
  const [loading, setLoading] = useState(false);



  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validate before submitting
    if (!validateForm()) {
      return;
    }

    try {
      setLoading(true);
      
      // Call backend API
      const response = await login({ 
        email: form.email, 
        password: form.password 
      });
      
      // Store token and user info
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("username", response.data.username);
      
      // Redirect to home
      navigate("/");
    } catch (err: any) {
      // Handle error
      const errorMessage = err?.response?.data?.message || "Login failed";
      setErrors(prev => ({ ...prev, submit: errorMessage }));
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
    
    // Clear submit error when user starts typing
    if (errors.submit) {
      setErrors(prev => ({ ...prev, submit: "" }));
    }
  };

  const validateForm = () => {
    const emailError = validateEmail(form.email);
    const passwordError = validatePassword(form.password);
    
    setErrors({
      email: emailError,
      password: passwordError,
      confirmPassword: "",
      submit: ""
    });
    
    return !emailError && !passwordError;
  };


  return (
  <div className="flex justify-center items-center min-h-screen bg-gray-800">
    <div className="flex flex-col items-center gap-8 p-12 w-full max-w-md">
      <h2 className="text-xl font-bold mb-4">Log In</h2>
      <form className="w-full flex flex-col gap-4" onSubmit={handleSubmit}>
      {/* Email input */}
      <div>
        <input 
          type="email" 
          name="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          className="block w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded text-white placeholder-gray-400"
        />
        {errors.email && <p className="text-red-600 text-sm mt-1">{errors.email}</p>}
      </div>

      {/* Password input */}
      <div>
        <input 
          type="password" 
          name="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          className="block w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded text-white placeholder-gray-400"
        />
        {errors.password && <p className="text-red-600 text-sm mt-1">{errors.password}</p>}
      </div>

      {/* Submit error */}
      {errors.submit && <p className="text-red-600">{errors.submit}</p>}

      {/* Submit button */}
      <button
        type="submit"
        disabled={loading}
        className="block w-full py-3 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:bg-blue-400"
      >
        {loading ? "Logging in..." : "Log In"}
      </button>
    </form>
          <button
          onClick={() => navigate('/sign-up')}
          className="text-blue-600 dark:text-blue-400 hover:underline text-sm"
        >
          Don't have an account?
        </button>
      </div>
  </div>
  );
};

export {LoginPage};