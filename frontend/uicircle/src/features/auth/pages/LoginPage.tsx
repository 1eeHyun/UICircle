import {useNavigate } from "react-router-dom";


const LoginPage = () => {

    const navigate = useNavigate();
    
    const handleSubmit = (e) => {
        // TODO: add logic to check for user account and log them in 
    e.preventDefault();

  };


    return (
    <div className="flex justify-center items-center min-h-screen bg-gray-800">
      <div className="flex flex-col items-center gap-8 p-12 w-full max-w-md">
        <h2 className="text-xl font-bold mb-4">Log In</h2>
        <form 
            className="w-full flex flex-col gap-4"
            onSubmit={handleSubmit}
            >
            <input 
                type='text' 
                name='usernameOrEmail'
                placeholder='Username or Email'
                className="block w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded text-white placeholder-gray-400"
                />
            <input 
                type='password' 
                name='password'
                placeholder='Password'
                className="block w-full px-4 py-3 bg-gray-700 border border-gray-600 rounded text-white placeholder-gray-400"
                />
            <button
                type="submit"
                className="block w-full py-3 bg-blue-600 text-white rounded hover:bg-blue-700"
            >
                Log In
            </button>
            {<p className="text-red-600 dark:text-red-400 mt-2"></p>}
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