'use strict';

const path = 'http://localhost:9000';
const content = {
  'Content-Type': 'application/json'
};

const isOk = (response) => {
  if (response.ok) return response;

  window.location.href = `${path}/error/${response.status}`;
};

const encodeParams = (...values) => values
  .map((value) => encodeURIComponent(value))
  .join('/');

const onQueryChange = (query) => {
  const button = document.getElementById('search_button');

  button.innerText = query.startsWith('http://') || query.startsWith('https://') ?
    'Fetch' :
    'Search';
};

const onQuery = async () => {
  const element = document.getElementById('query');
  const url = element.value;
  const [selector, ...array] = url.split(':');
  const query = array.join(':')

  switch (selector) {
    case 'http':
    case 'https':
      document.getElementById('search_button').style.display = 'none';
      document.getElementById('searching').style.display = 'block';

      const data = { url };

      if (data.url) {
        let request = fetch(`${path}/positions`, {
          method: 'POST',
          headers: { ...content },
          body: JSON.stringify(data)
        });

        let response = isOk(await request);
        await response.json();

        window.location.href = `${path}/positions`;
      }

      break;

    case 'u':
    case 'user':
      window.location.href = `${path}/users/${query}`;
      break;

    case 'c':
    case 'company':
      window.location.href = `${path}/companies/${query}`;
      break;

    default:
      element.value = '';
      return
  }
};

const onCreateUser = async () => {
  const attributeNodes = document.getElementsByName('attributes');
  const attributes = Array.from(attributeNodes);
  const username = document.getElementsByName('username')[0].value.toLowerCase();

  const data = {
    username: username || undefined,
    attributes: Object.assign(...attributes.map((attribute) => {
      const score = parseInt(attribute.value);

      return !!score ? {
        [attribute.dataset.lang]: score
      } : {};
    }))
  };

  if (data.username && !!Object.keys(data.attributes).length) {
    let request = fetch(`${path}/users`, {
      method: 'POST',
      headers: { ...content },
      body: JSON.stringify(data)
    });

    let response = isOk(await request);
    let json = await response.json();

    window.location.href = `${path}/users/${json.username}`;
  }
};

const onDestroyUser = async (username) => {
  const params = encodeParams(username);

  let request = fetch(`${path}/users/${params}`, {
    method: 'DELETE',
  });

  isOk(await request);

  window.location.href = `${path}/users`;
};

const onUpdatePositionDomain = async (id, domain) => {
  const element = document.getElementsByName('new_domain')[0];

  switch (element.dataset.update) {
    case "on":
      const params = encodeParams(id);

      const data = {
        domain: document.getElementsByName('new_domain')[0].value.trim()
      };

      if (data.domain && data.domain !== domain) {
        let request = fetch(`${path}/positions/${params}`, {
          method: 'POST',
          headers: { ...content },
          body: JSON.stringify(data)
        });

        let response = isOk(await request);
        await response.json();

        window.location.reload();
      }

      element.value = domain;
      element.style.display = 'none';
      element.dataset.update = 'off';
      break;

    default:
      element.value = domain;
      element.style.display = 'block';
      element.dataset.update = 'on';
  }
};

const onDestroyPosition = async (id) => {
  const params = encodeParams(id);

  let request = fetch(`${path}/positions/${params}`, {
    method: 'DELETE',
  });

  isOk(await request);

  window.location.href = `${path}/positions`;
};

const onCreateCompany = async () => {
  const domain = document.getElementsByName('domain')[0].value.toLowerCase() || undefined;
  const name = document.getElementsByName('company_name')[0].value || undefined;

  const data = { domain, name };

  if (data.domain) {
    let request = fetch(`${path}/companies`, {
      method: 'POST',
      headers: { ...content },
      body: JSON.stringify(data)
    });

    let response = isOk(await request);
    let json = await response.json();

    window.location.href = `${path}/companies/${json.domain}`;
  }
};

const onDestroyCompany = async (domain) => {
  const params = encodeParams(domain);

  let request = fetch(`${path}/companies/${params}`, {
    method: 'DELETE',
  });

  isOk(await request);

  window.location.href = `${path}/companies`;
};
