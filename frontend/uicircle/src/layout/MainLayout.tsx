import { useNavigate } from "react-router-dom";

const MainLayout = () => {
    const navigate = useNavigate();

    return (<>
        <div>
            <h2>Welcome</h2>
            <button onClick={() => navigate('/login')}>Go to login Page</button>
        </div>
    </>);
}

export {MainLayout};