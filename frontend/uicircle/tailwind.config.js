export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          light: '#ef4444', // red-500
          DEFAULT: '#dc2626', // red-600
          dark: '#b91c1c', // red-700
          darker: '#991b1b', // red-800
        },
        background: {
          light: '#ffffff',
          dark: '#1a1a1a',
        },
        surface: {
          light: '#f9fafb', // gray-50
          dark: '#262626', // neutral-800
        },
        border: {
          light: '#e5e7eb', // gray-200
          dark: '#404040', // neutral-700
        },
        uiBorder: {
          light: '#e5e7eb',
          dark: '#404040',
        },
      },
    },
  },
  plugins: [],
}
