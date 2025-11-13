import { useNavigate } from "react-router-dom"

const SignUpPage = () =>{

    const navigate = useNavigate();

    return (<>
    <h2>This is the signup page</h2>
    <button onClick={() => navigate('/')}>Back to Main Page</button>
    </>)
}

export {SignUpPage}