const apiUrl = `${Cypress.env("apiUrl")}`

function createGetCreditDataRequest(ssn) {
  return {
    failOnStatusCode: false,
    method: 'GET',
    url: `${apiUrl}/credit-data/${ssn}`,
  }
}

describe('Lookup Service - Level 1', () => {

  it('Provides a functional healthcheck', () => {
    cy.request({
      failOnStatusCode: false,
      method: 'GET',
      url: `${apiUrl}/ping`,
    }).then((response) => {
      assert.equal(response.status, 200, "Ping should return 200 status code")
    })
  })

  it('Repeated requests should give different responses when caching disabled in the first request', () => {
    var options = {
      failOnStatusCode: false,
      method: 'GET',
      url: `${apiUrl}/credit-data/424-11-9327`,
      headers: {'cache-control': 'no-store, 604800'}
    }
    cy.request(options).then((response1) => {
      assert.equal(response1.status, 200, "Getting aggregated credit data for ssn 424-11-9327 should return 200 status code")
      
      cy.request(createGetCreditDataRequest('424-11-9327')).then((response2) => {
        assert.equal(response2.status, 200, "Getting aggregated credit data for ssn 424-11-9327 should return 200 status code")

        assert.equal(response2.body.first_name, "Emma", "Data mismatch when returning aggregated credit data for ssn 424-11-9327")
        assert.notEqual(response1.body.address, response2.body.address, "The data should not be served from the cache in case of 'cache-control: no-store, max-age=604800'")
      })
    })
  })

  it('Can correctly aggregate and return Emma\'s credit data', () => {
    cy.request(createGetCreditDataRequest('424-11-9327')).then((response) => {
      assert.equal(response.status, 200, "Getting aggregated credit data for ssn 424-11-9327 should return 200 status code")
      assert.equal(response.body.first_name, "Emma", "Data mismatch when returning aggregated credit data for ssn 424-11-9327")
      assert.equal(response.body.last_name, "Gautrey", "Data mismatch when returning aggregated credit data for ssn 424-11-9327")
      expect(response.body.address).to.match(/\d{1,4} Westend Terrace$/)
      assert.equal(response.body.assessed_income, 60668, "Data mismatch when returning aggregated credit data for ssn 424-11-9327")
      assert.equal(response.body.balance_of_debt, 11585, "Data mismatch when returning aggregated credit data for ssn 424-11-9327")
      assert.equal(response.body.complaints, true, "Data mismatch when returning aggregated credit data for ssn 424-11-9327")
    })
  })
  
  it('Can correctly aggregate and return Billy\'s credit data', () => {
    cy.request(createGetCreditDataRequest('553-25-8346')).then((response) => {
      assert.equal(response.status, 200, "Getting aggregated credit data for ssn 424-11-9327 should return 200 status code")
      assert.equal(response.body.first_name, "Billy", "Data mismatch when returning aggregated credit data for ssn 553-25-8346")
      assert.equal(response.body.last_name, "Brinegar", "Data mismatch when returning aggregated credit data for ssn 553-25-8346")
      expect(response.body.address).to.match(/\d{1,4} Providence Lane La Puente, CA 91744$/)
      assert.equal(response.body.assessed_income, 89437, "Data mismatch when returning aggregated credit data for ssn 553-25-8346")
      assert.equal(response.body.balance_of_debt, 178, "Data mismatch when returning aggregated credit data for ssn 553-25-8346")
      assert.equal(response.body.complaints, false, "Data mismatch when returning aggregated credit data for ssn 553-25-8346")
    })
  })
  
  it('Can correctly aggregate and return Gail\'s credit data', () => {
    cy.request(createGetCreditDataRequest('287-54-7823')).then((response) => {
      assert.equal(response.status, 200, "Getting aggregated credit data for ssn 287-54-7823 should return 200 status code")
      assert.equal(response.body.first_name, "Gail", "Data mismatch when returning aggregated credit data for ssn 287-54-7823")
      assert.equal(response.body.last_name, "Shick", "Data mismatch when returning aggregated credit data for ssn 287-54-7823")
      expect(response.body.address).to.match(/\d{1,4} Rainbow Drive Canton, OH 44702$/)
      assert.equal(response.body.assessed_income, 42301, "Data mismatch when returning aggregated credit data for ssn 287-54-7823")
      assert.equal(response.body.balance_of_debt, 23087, "Data mismatch when returning aggregated credit data for ssn 287-54-7823")
      assert.equal(response.body.complaints, true, "Data mismatch when returning aggregated credit data for ssn 287-54-7823")
    })
  })
  
  it('Can handle requests for non-existent SSNs', () => {
    cy.request(createGetCreditDataRequest('000-00-0000')).then((response) => {
      assert.equal(response.status, 404, "Getting aggregated credit data for non-existent SSN should return 404 status code")
    })
  })
 
  
  it('Repeated requests should give the same response body thanks to caching', () => {
    cy.request(createGetCreditDataRequest('424-11-9327')).then((response1) => {
      assert.equal(response1.status, 200, "Getting aggregated credit data for ssn 424-11-9327 should return 200 status code")

      assert.equal(response1.body.first_name, "Emma", "Data mismatch when returning aggregated credit data for ssn 424-11-9327")
      expect(response1.body.address).to.match(/\d{1,4} Westend Terrace$/)

      cy.request(createGetCreditDataRequest('424-11-9327')).then((response2) => {
        assert.equal(response2.status, 200, "Getting aggregated credit data for ssn 424-11-9327 should return 200 status code")

        assert.equal(response2.body.first_name, "Emma", "Data mismatch when returning aggregated credit data for ssn 424-11-9327")
        expect(response2.body.address).to.match(/\d{1,4} Westend Terrace$/)
  
        assert.equal(response1.body.address, response2.body.address, "Subsequent requests to fetch the same data should be served from the service's DB.")
      })
    })
  })

  
  
  it('Repeated requests should give different responses when max-age exceeded', () => {
    cy.request(createGetCreditDataRequest('424-11-9327')).then((response1) => {
      assert.equal(response1.status, 200, "Getting aggregated credit data for ssn 424-11-9327 should return 200 status code")

      cy.wait(4000)

      var options = {
        failOnStatusCode: false,
        method: 'GET',
        url: `${apiUrl}/credit-data/424-11-9327`,
        headers: {'cache-control': 'private, max-age:3'}
      }
      cy.request(options).then((response2) => {
        assert.equal(response2.status, 200, "Getting aggregated credit data for ssn 424-11-9327 should return 200 status code")

        assert.equal(response2.body.first_name, "Emma", "Data mismatch when returning aggregated credit data for ssn 424-11-9327")
        assert.notEqual(response1.body.address, response2.body.address, "The data should be served fresh from server with 'cache-control: private, max-age=3'")
      })
    })
  })

  
  it('Repeated requests should give different responses when no-store returned from personal-details server', () => {
    cy.request(createGetCreditDataRequest('287-54-7823')).then((response1) => {
      assert.equal(response1.status, 200, "Getting aggregated credit data for ssn 287-54-7823 should return 200 status code")

      cy.request(createGetCreditDataRequest('287-54-7823')).then((response2) => {
        assert.equal(response2.body.first_name, "Gail", "Data mismatch when returning aggregated credit data for ssn 287-54-7823")
        assert.notEqual(response1.body.address, response2.body.address, "The data should be served fresh since the response from personal-details server contains 'no-store'")
      })
    })
  })
  
  it('Repeated requests should give different responses when max-age returned from personal-details server is exceeded', () => {
    cy.request(createGetCreditDataRequest('553-25-8346')).then((response1) => {
      assert.equal(response1.status, 200, "Getting aggregated credit data for ssn 553-25-8346 should return 200 status code")

      cy.wait(6000)

      cy.request(createGetCreditDataRequest('553-25-8346')).then((response2) => {
        assert.equal(response2.body.first_name, "Billy", "Data mismatch when returning aggregated credit data for ssn 553-25-8346")
        assert.notEqual(response1.body.address, response2.body.address, "The data should be served fresh since the response from personal-details server contains 'max-age=5'")
      })
    })
  })
  
})
