import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/AuthService";

const LoginPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    emailOrUsername: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
    if (error) setError("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!form.emailOrUsername || !form.password) {
      setError("Please fill in all fields");
      return;
    }

    try {
      setLoading(true);
      const response = await login(form);
      
      if (response.success && response.data) {
        localStorage.setItem("token", response.data.accessToken);
        localStorage.setItem("username", response.data.user.username);
        navigate("/home");
      }
    } catch (err: any) {
      setError(err?.response?.data?.message || "Login failed. Please check your credentials.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      {/* Left Side - Brand */}
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-primary via-primary-dark to-primary-darker p-12 flex-col justify-between">
        <div>
          <h1 className="text-5xl font-bold text-background-light mb-4">UICircle</h1>          
        </div>                            
      </div>

      {/* Right Side - Login Form */}
      <div className="flex-1 flex items-center justify-center p-8 bg-surface-light">
        <div className="w-full max-w-md">
          <div className="lg:hidden text-center mb-8">
            <h1 className="text-4xl font-bold text-primary mb-2">UICircle</h1>            
          </div>

          <div className="bg-background-light rounded-2xl shadow-xl p-8 border border-border-light">          
            <form onSubmit={handleSubmit} className="space-y-5">
              <div>                 
                <input
                  id="emailOrUsername"
                  type="text"
                  name="emailOrUsername"
                  placeholder="Enter your username or email"
                  value={form.emailOrUsername}
                  onChange={handleChange}
                  className="w-full px-4 py-3 bg-surface-light border border-border-light rounded-xl text-gray-900 placeholder-gray-400 focus:bg-background-light focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition"
                />
              </div>

              <div>
                <input
                  id="password"
                  type="password"
                  name="password"
                  placeholder="Enter your password"
                  value={form.password}
                  onChange={handleChange}
                  className="w-full px-4 py-3 bg-surface-light border border-border-light rounded-xl text-gray-900 placeholder-gray-400 focus:bg-background-light focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition"
                />
              </div>
              
              {error && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                  <p className="text-red-700 text-sm">{error}</p>
                </div>
              )}
              
              <button
                type="submit"
                disabled={loading}
                className="w-full py-3.5 bg-gradient-to-r from-primary to-primary-dark hover:from-primary-dark hover:to-primary-darker text-background-light rounded-xl font-semibold shadow-lg shadow-primary/30 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 transform hover:scale-[1.02]"
              >
                {loading ? (
                  <span className="flex items-center justify-center gap-2">
                    <svg className="animate-spin h-5 w-5" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Signing In...
                  </span>
                ) : (
                  "Sign In"
                )}
              </button>
            </form>
            
            <div className="mt-6 text-center">
              <p className="text-gray-600 text-sm">
                Don't have an account?{" "}
                <button
                  onClick={() => navigate("/signup")}
                  className="text-primary hover:text-primary-dark font-semibold hover:underline"
                >
                  Sign Up
                </button>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export { LoginPage };