import './App.css'
import Router from './components/Router'
import { ThemeProvider } from './components/ThemeProvider'

function App() {

  return (
    <>
    <ThemeProvider>
      <Router/>
    </ThemeProvider>
    </>
  )
}

export default App
