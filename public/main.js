'use strict';

const path = 'http://localhost:9000';
const content = {
  'Content-Type': 'application/json'
};

const isOk = (response) => {
  if (response.ok) return response;

  window.location.href = `${path}/error/${response.status}`;
};

const onCreateUser = async () => {
  const attributeNodes = document.getElementsByName('attributes');
  const username = document.getElementsByName('username')[0].value.toLowerCase();
  const attributes = Array.from(attributeNodes);

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
  let request = fetch(`${path}/users/${username}`, {
    method: 'DELETE',
  });

  isOk(await request);

  window.location.href = `${path}/users`;
}
