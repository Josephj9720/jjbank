import './App.css'
import Router from './components/routing/Router'
import { ThemeProvider } from './components/providers/ThemeProvider'
import { CssBaseline } from '@mui/material'
import { AuthenticationProvider } from './components/providers/AuthenticationProvider'

function App() {

  return (
    <>
      <AuthenticationProvider>
        <ThemeProvider>
          <CssBaseline/>
          <Router/>
        </ThemeProvider>
      </AuthenticationProvider>
    </>
  )
}

export default App
