export const Home = () => {
  return (
    <div className="bg-white">
      <header className="absolute inset-x-0 top-0 z-50">
        <nav
          className="flex items-center justify-center p-6 lg:px-8"
          aria-label="Global"
        >
          <div className="flex items-center gap-4">
            <div>
              <span className="sr-only">Breeze</span>
              <img className="h-32 w-32" src="./logo.png" alt="" />
            </div>
            <h1 className="font-serif text-4xl pt-8">Breeze</h1>
          </div>
        </nav>
      </header>

      <main>
        <div className="pt-20 bg-paper bg-cover">
          <div className="mt-12 mx-auto max-w-7xl px-6 py-24 flex flex-col items-center gap-8">
            <p className="text-5xl leading-8 font-hand-written ">
              Use all your devices seamlessly.
            </p>
            <img src="./all-devices.png" alt="" />
          </div>
        </div>
      </main>

      <footer className="bg-gray-900" aria-labelledby="footer-heading">
        <h2 id="footer-heading" className="sr-only">
          Footer
        </h2>
        <div className="mx-auto max-w-7xl px-6 pb-8 pt-16 sm:pt-24 lg:px-8 lg:pt-32">
          <div className="xl:grid xl:grid-cols-3 xl:gap-8">
            <img className="h-7" src="./logo.png" alt="Breeze" />
          </div>
          <div className="mt-8 border-t border-white/10 pt-8 md:flex md:items-center md:justify-between">
            <p className="mt-8 text-xs leading-5 text-gray-400 md:order-1 md:mt-0">
              &copy; 2023 Breeze - All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
};
