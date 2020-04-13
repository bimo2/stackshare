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
  const button = document.getElementById("search_button");

  button.innerText = query.startsWith('http://') || query.startsWith('https://') ?
    'Fetch' :
    'Search';
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

  return false;
};

const onDestroyUser = async (username) => {
  const params = encodeParams(username);

  let request = fetch(`${path}/users/${params}`, {
    method: 'DELETE',
  });

  isOk(await request);

  window.location.href = `${path}/users`;
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

  return false;
};
