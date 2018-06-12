import { TestBed, inject } from '@angular/core/testing';

import { KcdocumentService } from './kcdocument.service';

describe('KcdocumentService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [KcdocumentService]
    });
  });

  it('should be created', inject([KcdocumentService], (service: KcdocumentService) => {
    expect(service).toBeTruthy();
  }));
});
