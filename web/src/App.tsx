import { Routes, Route, Link } from "react-router-dom";
import { SessionPage } from "./pages/SessionPage";
import { Home } from "./pages/Home";

function App() {
  return (
    <Routes>
      <Route path="/">
        <Route index element={<Home />} />
        <Route path="session/:sessionId" element={<SessionPage />} />
        <Route path="*" element={<NoMatch />} />
      </Route>
    </Routes>
  );
}

export default App;

function NoMatch() {
  return (
    <div>
      <h2>Nothing to see here!</h2>
      <p>
        <Link to="/">Go to the home page</Link>
      </p>
    </div>
  );
}
