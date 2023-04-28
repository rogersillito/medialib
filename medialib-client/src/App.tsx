import React from 'react';
// import logo from './logo.svg';
import DirectoryView from "./components/DirectoryView";
import './App.css';

function Test() {
  return (<p>hi</p>)
}

function App() {
  return (
    <div className="App">
      <header className="App-header">
        {/* <img src={logo} className="App-logo" alt="logo" /> */}
        <h2>
          Medialib
        </h2>
        <Test></Test> 
        {/* TODO: this is broken atm... */}
        {/* <DirectoryView></DirectoryView> */}
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;
