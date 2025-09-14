import { Outlet } from "react-router";
import Navbar from "./Navbar";

const Layout = () => {
  return (
    <>
      <Navbar/> {/* same navbar on every page */}
      <main>
        <Outlet/> {/* Renders the current page component */}
      </main>
    </>
  );
}

export default Layout;