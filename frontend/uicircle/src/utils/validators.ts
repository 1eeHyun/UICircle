export const validateEmail = (email: string): string => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!email) return "Email is required";
  if (!emailRegex.test(email)) return "Invalid email format";
  return "";
};

export const validatePassword = (password: string): string => {
  if (!password) return "Password is required";
  if (password.length < 8) return "Password must be at least 8 characters";
  if (!/(?=.*[a-z])/.test(password)) return "Password must contain a lowercase letter";
  if (!/(?=.*[A-Z])/.test(password)) return "Password must contain an uppercase letter";
  if (!/(?=.*\d)/.test(password)) return "Password must contain a number";
  return "";
};