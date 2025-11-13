import { useState } from 'react';
import reactLogo from './assets/react.svg';
import viteLogo from '/vite.svg';
import { AppRouter } from './routes/AppRouter.tsx';
import { Router, RouterProvider } from 'react-router-dom';
import './App.css';



function App() {
  const [count, setCount] = useState(0)

  return (<>
    <RouterProvider router={AppRouter}/>
  </>);
}

export default App
